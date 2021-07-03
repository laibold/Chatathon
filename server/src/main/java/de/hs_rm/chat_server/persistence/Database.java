package de.hs_rm.chat_server.persistence;

import de.hs_rm.chat_server.domain.user.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Database instance;

    private Connection connection = null;

    private Database() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        openConnection();

        try {
            createUserTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Database getInstance() {
        if (Database.instance == null) {
            Database.instance = new Database();
        }

        return Database.instance;
    }

    private void openConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:h2:./db/rat-chat;MODE=MYSQL", "", "pwd");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createUserTable() throws SQLException {
        var statement = connection.createStatement();
        statement.execute(
            "CREATE TABLE IF NOT EXISTS user(" +
                "id int NOT NULL PRIMARY KEY auto_increment, " +
                "username varchar(255) NOT NULL, " +
                "password varchar(255) NOT NULL" +
                ")"
        );
        statement.close();

        connection.close();
    }

    public void insertUser(String username, String password) throws SQLException {
        openConnection();
        var statement = connection.prepareStatement(
            "INSERT INTO user (username, password) VALUES (?, ?)"
        );
        statement.setString(1, username);
        statement.setString(2, password);
        statement.execute();

        statement.close();
        connection.close();
    }

    public User getUser(String username) throws SQLException {
        openConnection();

        var statement = connection.prepareStatement(
            "SELECT * FROM USER WHERE username=? LIMIT 1"
        );
        statement.setString(1, username);
        statement.executeQuery();

        var resultSet = statement.executeQuery();
        if (resultSet.next()) {
            var queriedUsername = resultSet.getString("username");
            var queriedPassword = resultSet.getString("password");

            statement.close();
            connection.close();

            return new User(queriedUsername, queriedPassword);
        }
        return null;
    }

}
