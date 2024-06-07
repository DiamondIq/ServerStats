package me.diamond.serverstats.database;

import me.diamond.serverstats.utils.TimeUtils;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtils {

    private static final Connection connection = Database.get();


    public static class MostPlayersPerDay {
        public static void createMostPlayerPerDayTable() throws SQLException {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS most_players_per_day (" +
                    "at TIMESTAMP PRIMARY KEY, " +
                    "amount INTEGER NOT NULL)");
        }

        public static int getMostPlayersInADay(Date at) {
            String query = "SELECT * FROM most_players_per_day WHERE at >= ? AND at < ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setTimestamp(1, new Timestamp(TimeUtils.getStartOfDay(at)));
                statement.setTimestamp(2, new Timestamp(TimeUtils.getEndOfDay(at)));
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(2);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return 0;
        }

        public static void updateMostPlayersInADay(int players, Timestamp at) throws SQLException {
            String query1 = "SELECT * FROM most_players_per_day WHERE at >= ? AND at < ?";
            String query2 = "DELETE FROM most_players_per_day WHERE at >= ? AND at < ?";
            String query3 = "INSERT INTO most_players_per_day (at, amount) VALUES (?, ?)";
            boolean b;
            try (PreparedStatement statement = connection.prepareStatement(query1)) {
                statement.setTimestamp(1, new Timestamp(TimeUtils.getStartOfDay(at)));
                statement.setTimestamp(2, new Timestamp(TimeUtils.getEndOfDay(at)));
                ResultSet resultSet = statement.executeQuery();
                b = resultSet.next();
            }
            if (b) {
                try (PreparedStatement statement = connection.prepareStatement(query2)) {
                    statement.setTimestamp(1, new Timestamp(TimeUtils.getStartOfDay(at)));
                    statement.setTimestamp(2, new Timestamp(TimeUtils.getEndOfDay(at)));
                    statement.executeUpdate();
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(query3)) {
                statement.setTimestamp(1, at);
                statement.setInt(2, players);
                statement.executeUpdate();
            }
        }

        public static Timestamp getMostPlayersInADayTime(Date at) {
            String query = "SELECT * FROM most_players_per_day WHERE at >= ? AND at < ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setTimestamp(1, new Timestamp(TimeUtils.getStartOfDay(at)));
                statement.setTimestamp(2, new Timestamp(TimeUtils.getEndOfDay(at)));
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getTimestamp("at");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class MostPlayersPerHour {
        public static void createMostPlayerPerHourTable() throws SQLException {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS most_players_per_hour (" +
                    "at TIMESTAMP PRIMARY KEY, " +
                    "amount INTEGER NOT NULL)");
        }
        public static void setOnlinePlayersPerHour(int i, Timestamp at) throws SQLException {
            String query = "INSERT INTO most_players_per_hour (at, amount) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setTimestamp(1, at);
                statement.setInt(2, i);
                statement.executeUpdate();
            }
        }
    }

    public static class Kills {
        public static void createKillsTable() throws SQLException {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS kills_per_day (" +
                    "at DATE PRIMARY KEY, " +
                    "killer TEXT NOT NULL, " +
                    "amount INTEGER NOT NULL)");
        }

        public static void addKills(Player p, Date at) throws SQLException {
            String query1 = "REPLACE INTO kills_per_day (at, killer) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query1)) {
                statement.setDate(1, at);
                statement.setString(2, p.getUniqueId().toString());
                statement.executeUpdate();
            }
        }

        public static int getKills(Date at, Player p) throws SQLException {
            String query1 = "SELECT COUNT AS count FROM kills_per_day WHERE at = ? AND killer = ?";
            try (PreparedStatement statement = connection.prepareStatement(query1)) {
                statement.setDate(1, at);
                statement.setString(2, p.getUniqueId().toString());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
            return 0;
        }
    }


    public static class Joins {
        public static void createJoinsTable() throws SQLException {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS joins (" +
                    "at DATE PRIMARY KEY, " +
                    "player TEXT NOT NULL)");
        }

        public static void addJoin(int i, Date at) throws SQLException {
            String query = "INSERT INTO joins (at, player) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDate(1, at);
                statement.setInt(2, i);
                statement.executeUpdate();
            }

        }

        public static int getJoinsPerDay(Date at) throws SQLException {
            String query1 = "SELECT * FROM joins_per_day WHERE at = ?";
            try (PreparedStatement statement = connection.prepareStatement(query1)) {
                statement.setDate(1, at);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("amount");
                }
            }
            return 0;
        }
    }


    public static Map<Integer, Timestamp> getValuesBetweenDates(String table, Timestamp startDate, Timestamp endDate) throws SQLException {
        Map<Integer, Timestamp> valuesPerDay = new HashMap<>();
        PreparedStatement statement = connection.prepareStatement("SELECT joins, at FROM " + table + " WHERE at BETWEEN ? AND ?");
        statement.setTimestamp(1, startDate);
        statement.setTimestamp(2, endDate);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int amount = resultSet.getInt("amount");
            Timestamp timestamp = resultSet.getTimestamp("at");
            valuesPerDay.put(amount, timestamp);
        }
        return valuesPerDay;
    }

    public static Map<Integer, Timestamp> getValuesInDate(String table, Date date) throws SQLException {
        Map<Integer, Timestamp> valuesPerDay = new HashMap<>();
        PreparedStatement statement = connection.prepareStatement("SELECT amount, at FROM " + table + " WHERE at > ? AND at < ?");
        Timestamp startTime = new Timestamp(TimeUtils.getStartOfDay(date));
        Timestamp endTime = new Timestamp(TimeUtils.getEndOfDay(date));
        statement.setTimestamp(1, startTime);
        statement.setTimestamp(2, endTime);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int amount = resultSet.getInt("amount");
            Timestamp timestamp = resultSet.getTimestamp("at");
            valuesPerDay.put(amount, timestamp);
            System.out.println(valuesPerDay);
        }
        return valuesPerDay;
    }

}
