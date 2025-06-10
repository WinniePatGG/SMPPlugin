package de.winniepat.SMPPlugin.waypoints;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.*;

public class WaypointsDatabase {
    private final String url = "jdbc:sqlite:plugins/SMPPlugin/waypoints.db";

    public WaypointsDatabase() {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS waypoints (
                    player_uuid TEXT,
                    name TEXT,
                    world TEXT,
                    x DOUBLE,
                    y DOUBLE,
                    z DOUBLE
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean saveWaypoint(UUID playerUuid, Waypoint waypoint) {
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement check = conn.prepareStatement("""
            SELECT 1 FROM waypoints WHERE player_uuid = ? AND name = ?
        """)) {
            check.setString(1, playerUuid.toString());
            check.setString(2, waypoint.getName());
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO waypoints (player_uuid, name, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?)
        """)) {
            ps.setString(1, playerUuid.toString());
            ps.setString(2, waypoint.getName());
            ps.setString(3, waypoint.getLocation().getWorld().getName());
            ps.setDouble(4, waypoint.getLocation().getX());
            ps.setDouble(5, waypoint.getLocation().getY());
            ps.setDouble(6, waypoint.getLocation().getZ());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Waypoint> getWaypoints(UUID playerUuid) {
        List<Waypoint> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement ps = conn.prepareStatement("""
                SELECT name, world, x, y, z FROM waypoints WHERE player_uuid = ?
            """)) {
            ps.setString(1, playerUuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String world = rs.getString("world");
                double x = rs.getDouble("x"), y = rs.getDouble("y"), z = rs.getDouble("z");
                list.add(new Waypoint(name, new Location(Bukkit.getWorld(world), x, y, z)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}