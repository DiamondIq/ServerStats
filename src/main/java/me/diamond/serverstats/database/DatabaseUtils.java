package me.diamond.serverstats.database;

import me.diamond.serverstats.utils.TimeUtils;

import java.sql.*;

public class DatabaseUtils {
    public static void createMostPlayerPerDayTable() throws SQLException {
        Connection connection = Database.get();
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS most_players_per_day (" +
                "when TIMESTAMP PRIMARY KEY, " +
                "amount INTEGER NOT NULL)");
    }

    public static void createMostPlayerPerHourTable() throws SQLException {
        Connection connection = Database.get();
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS most_players_per_hour (" +
                "when TIMESTAMP PRIMARY KEY, " +
                "amount INTEGER NOT NULL)");
    }

    public static void createJoinsPerDayTable() throws SQLException {
        Connection connection = Database.get();
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS joins_per_day (" +
                "when DATE PRIMARY KEY, " +
                "joins INTEGER NOT NULL)");
    }

    public static int getMostPlayersInADay(Date at) {
        Connection connection = Database.get();
        String query = "SELECT * FROM most_players WHERE when >= ? AND when < ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setTimestamp(1, new Timestamp(TimeUtils.getStartOfDay(at)));
            statement.setTimestamp(2, new Timestamp(TimeUtils.getEndOfDay(at)));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public static void updateMostPlayersInADay(int players, Timestamp at) throws SQLException {
        Connection connection = Database.get();
        String query1 = "SELECT * FROM most_player_per_day";
        String query2 = "DELETE FROM most_player_per_day WHERE when = ?";
        String query3 = "INSERT INTO most_players_per_day (when, amount) VALUES (?, ?)";
        boolean b = false;
        try (PreparedStatement statement = connection.prepareStatement(query1)) {
            ResultSet resultSet = statement.executeQuery();
            b = resultSet.next();
        }
        if (b){
            try (PreparedStatement statement = connection.prepareStatement(query2)) {
                statement.setTimestamp(1, at);
                statement.executeUpdate();
            }
        }
        try (PreparedStatement statement = connection.prepareStatement(query3)) {
            statement.setTimestamp(1, at);
            statement.setInt(2, players);
            statement.executeUpdate();
        }
    }

    public static void addJoinsPerDay(int i, Date at) throws SQLException {
        Connection connection = Database.get();
        String query = "INSERT INTO joins_per_day (when, amount) VALUES (?, ?)";
        String query1 = "REPLACE INTO joins_per_day (when, amount) VALUES (?, ?)";
        if (getJoinsPerDay(at) == 0){
            try (PreparedStatement statement = connection.prepareStatement(query)){
                statement.setDate(1, at);
                statement.setInt(2, i);
                statement.executeUpdate();
            }
        } else {
            try (PreparedStatement statement = connection.prepareStatement(query1)){
                statement.setDate(1, at);
                statement.setInt(2, getJoinsPerDay(at) + i);
                statement.executeUpdate();
            }
        }
    }

    public static int getJoinsPerDay(Date at) throws SQLException {
        Connection connection = Database.get();
        String query1 = "SELECT * FROM joins_per_day WHERE when = ?";
        try (PreparedStatement statement = connection.prepareStatement(query1)) {
            statement.setDate(1, at);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("joins");
            }
        }
        return 0;
    }
}
