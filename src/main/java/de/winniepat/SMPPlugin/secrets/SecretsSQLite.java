package de.winniepat.SMPPlugin.secrets;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SecretsSQLite {
    private static Connection connection;

    public static void initDatabase(JavaPlugin plugin) {
        try {
            File dbFile = new File(plugin.getDataFolder(), "data.db");
            dbFile.getParentFile().mkdirs();
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS secrets (
                        uuid TEXT PRIMARY KEY,
                        secretA INTEGER DEFAULT 0,
                        secretB INTEGER DEFAULT 0
                    );
                """);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasClaimedSecret(UUID uuid, String secret) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT " + secret + " FROM secrets WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(secret) == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void markSecretClaimed(UUID uuid, String secret) {
        try {
            // First ensure row exists
            try (PreparedStatement insert = connection.prepareStatement("INSERT OR IGNORE INTO secrets(uuid) VALUES (?)")) {
                insert.setString(1, uuid.toString());
                insert.executeUpdate();
            }

            try (PreparedStatement update = connection.prepareStatement("UPDATE secrets SET " + secret + " = 1 WHERE uuid = ?")) {
                update.setString(1, uuid.toString());
                update.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasClaimedBoth(UUID uuid) {
        return hasClaimedSecret(uuid, "secretA") && hasClaimedSecret(uuid, "secretB");
    }
}
