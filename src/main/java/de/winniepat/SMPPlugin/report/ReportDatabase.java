package de.winniepat.SMPPlugin.report;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.*;

public class ReportDatabase {

    private final JavaPlugin plugin;
    private Connection connection;

    public ReportDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
        connect();
        createTable();
    }

    private void connect() {
        try {
            File folder = plugin.getDataFolder();
            if (!folder.exists()) folder.mkdirs();

            String url = "jdbc:sqlite:" + folder.getAbsolutePath() + "/reports.db";
            connection = DriverManager.getConnection(url);
            plugin.getLogger().info("Report fatabase successfully connectes.");
        } catch (SQLException e) {
            plugin.getLogger().warning("Error while connecting to the database: " + e.getMessage());
        }
    }

    private void createTable() {
        if (connection == null) return;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    reporter TEXT,
                    reported TEXT,
                    reason TEXT,
                    timestamp TEXT
                );
            """);
        } catch (SQLException e) {
            plugin.getLogger().warning("Error while crating the table: " + e.getMessage());
        }
    }

    public void insertReport(String reporter, String reported, String reason) {
        if (connection == null) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO reports (reporter, reported, reason, timestamp) VALUES (?, ?, ?, datetime('now'))")) {
            ps.setString(1, reporter);
            ps.setString(2, reported);
            ps.setString(3, reason);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Error while saving the report: " + e.getMessage());
        }
    }

    public List<Map<String, String>> getAllReports() {
        List<Map<String, String>> reports = new ArrayList<>();
        if (connection == null) return reports;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM reports ORDER BY id DESC")) {
            while (rs.next()) {
                Map<String, String> entry = new HashMap<>();
                entry.put("id", rs.getString("id"));
                entry.put("reporter", rs.getString("reporter"));
                entry.put("reported", rs.getString("reported"));
                entry.put("reason", rs.getString("reason"));
                entry.put("timestamp", rs.getString("timestamp"));
                reports.add(entry);
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Error while loading the reports: " + e.getMessage());
        }
        return reports;
    }

    public Map<String, String> getReportById(String id) {
        if (connection == null) return null;

        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM reports WHERE id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Map<String, String> r = new HashMap<>();
                r.put("id", rs.getString("id"));
                r.put("reporter", rs.getString("reporter"));
                r.put("reported", rs.getString("reported"));
                r.put("reason", rs.getString("reason"));
                r.put("timestamp", rs.getString("timestamp"));
                return r;
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Fehler beim Laden Report ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    public void deleteReportById(String id) {
        if (connection == null) return;

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM reports WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Fehler beim Löschen Report ID " + id + ": " + e.getMessage());
        }
    }
}
