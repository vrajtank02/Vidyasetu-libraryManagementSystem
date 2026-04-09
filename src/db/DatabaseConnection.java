package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database credentials and URL
    private static final String URL = "jdbc:mysql://localhost:3306/vidyasetu_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // The single connection instance
    private static Connection connection = null;

    private DatabaseConnection() {
    }

    public static Connection getConnection() {
        try {
            // Check if connection is null or closed. If it is, open a new one.
            if (connection == null || connection.isClosed()) {
                // Load the driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }
}