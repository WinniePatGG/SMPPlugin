package de.winniepat.SMPPlugin.suggestions;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class SuggestionManager {
    private final Connection connection;

    public SuggestionManager(File file) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        createTable();
    }

    private void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS suggestions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    uuid TEXT NOT NULL,
                    name TEXT NOT NULL,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    status TEXT NOT NULL,
                    timestamp TEXT NOT NULL
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public Suggestion addSuggestion(UUID uuid, String name, String title, String content) {
        String sql = "INSERT INTO suggestions(uuid, name, title, content, status, timestamp) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, name);
            stmt.setString(3, title);
            stmt.setString(4, content);
            stmt.setString(5, SuggestionStatus.OPEN.name());
            stmt.setString(6, Instant.now().toString());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                return new Suggestion(id, uuid, name, title, content);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Collection<Suggestion> getAllSuggestions() {
        List<Suggestion> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM suggestions")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                String title = rs.getString("title");
                String content = rs.getString("content");
                SuggestionStatus status = SuggestionStatus.valueOf(rs.getString("status"));
                Suggestion suggestion = new Suggestion(id, uuid, name, title, content);
                suggestion.setStatus(status);
                list.add(suggestion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Suggestion getSuggestionById(int id) {
        String sql = "SELECT * FROM suggestions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                String title = rs.getString("title");
                String content = rs.getString("content");
                SuggestionStatus status = SuggestionStatus.valueOf(rs.getString("status"));
                Suggestion suggestion = new Suggestion(id, uuid, name, title, content);
                suggestion.setStatus(status);
                return suggestion;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateStatus(int id, SuggestionStatus status) {
        String sql = "UPDATE suggestions SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteSuggestion(int id) {
        String sql = "DELETE FROM suggestions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
