package net.minetaria.friendsdatacollector.utils;

import net.minetaria.friendsdatacollector.FriendsDataCollector;

import java.sql.*;

public class SQLUtil {

    private Connection connection;

    public SQLUtil(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            FriendsDataCollector.getInstance().getProxy().stop();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public int executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(query);
    }

    public void close() throws SQLException {
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }
}
