package me.diamond.serverstats.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection connection = null;
    public Database(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        DatabaseUtils.createMostPlayerPerDayTable();
        DatabaseUtils.createMostPlayerPerHourTable();
    }

    public static Connection get(){
        return connection;
    }

}
