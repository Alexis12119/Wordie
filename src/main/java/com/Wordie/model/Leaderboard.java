package com.Wordie.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    private static final String DB_URL = "jdbc:sqlite:wordie.db";

    public record ScoreRecord(String name, Difficulty difficulty, int attempts, String playedAt) {}

    public Leaderboard() {
        try (Connection connection = connect();
             Statement createTableStatement = connection.createStatement()) {
            createTableStatement.execute("""
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
        List<ScoreRecord> topScores = getTop(difficulty);
        if (topScores.size() < 10) return true;
        return attempts < topScores.get(topScores.size() - 1).attempts();
    }

    public void save(String name, Difficulty difficulty, int attempts) {
        String insertSql = "INSERT INTO leaderboard (name, difficulty, attempts) VALUES (?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, difficulty.name());
            preparedStatement.setInt(3, attempts);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Leaderboard save error: " + e.getMessage());
        }
    }

    public List<ScoreRecord> getTop(Difficulty difficulty) {
        List<ScoreRecord> topScores = new ArrayList<>();
        String querySql = "SELECT name, difficulty, attempts, played_at FROM leaderboard WHERE difficulty = ? ORDER BY attempts ASC, played_at ASC LIMIT 10";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(querySql)) {
            preparedStatement.setString(1, difficulty.name());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                topScores.add(new ScoreRecord(
                    resultSet.getString("name"),
                    Difficulty.valueOf(resultSet.getString("difficulty")),
                    resultSet.getInt("attempts"),
                    resultSet.getString("played_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Leaderboard query error: " + e.getMessage());
        }
        return topScores;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
