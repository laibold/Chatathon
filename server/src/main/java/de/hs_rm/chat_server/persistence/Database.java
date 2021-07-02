package de.hs_rm.chat_server.persistence;

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

        try {
            connection = DriverManager.getConnection("jdbc:h2:./db/rat-chat", "", "pwd");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            createUser();
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

    private void createUser() throws SQLException {
        var statement = connection.createStatement();
        statement.execute("create table if not exists user(username varchar(255), password varchar(255))");
        statement.close();
    }

}
