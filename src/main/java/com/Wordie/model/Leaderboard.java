package com.Wordie.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    private static final String DB_URL = "jdbc:sqlite:wordie.db";

    public record Entry(String name, Difficulty difficulty, int attempts, String playedAt) {}

    public Leaderboard() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS leaderboard (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    difficulty TEXT NOT NULL,
                    attempts INTEGER NOT NULL,
                    played_at TEXT DEFAULT (datetime('now','localtime'))
                )
            """);
        } catch (SQLException e) {
            System.err.println("Leaderboard init error: " + e.getMessage());
        }
    }

    public boolean isTopScore(Difficulty difficulty, int attempts) {
        List<Entry> top = getTop(difficulty);
        if (top.size() < 10) return true;
        return attempts < top.get(top.size() - 1).attempts();
    }

    public void save(String name, Difficulty difficulty, int attempts) {
        String sql = "INSERT INTO leaderboard (name, difficulty, attempts) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, difficulty.name());
            ps.setInt(3, attempts);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Leaderboard save error: " + e.getMessage());
        }
    }

    public List<Entry> getTop(Difficulty difficulty) {
        List<Entry> entries = new ArrayList<>();
        String sql = "SELECT name, difficulty, attempts, played_at FROM leaderboard WHERE difficulty = ? ORDER BY attempts ASC, played_at ASC LIMIT 10";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, difficulty.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                entries.add(new Entry(
                    rs.getString("name"),
                    Difficulty.valueOf(rs.getString("difficulty")),
                    rs.getInt("attempts"),
                    rs.getString("played_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Leaderboard query error: " + e.getMessage());
        }
        return entries;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
