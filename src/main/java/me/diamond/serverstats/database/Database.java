package me.diamond.serverstats.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection connection = null;

    public static Connection get() {
        return connection;
    }

    public static void init(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        DatabaseUtils.MostPlayersPerDay.createMostPlayerPerDayTable();
        DatabaseUtils.MostPlayersPerHour.createMostPlayerPerHourTable();
        DatabaseUtils.Joins.createJoinsTable();
        DatabaseUtils.Kills.createKillsTable();
    }
}
