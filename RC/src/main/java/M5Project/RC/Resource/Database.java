package M5Project.RC.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import M5Project.RC.model.Player;
import com.google.gson.Gson;

import javax.xml.crypto.Data;

public class Database {
    //information for methods to access database
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String host = "bronto.ewi.utwente.nl";
    static final String dbName = "dab_di20212b_100";
    static final String DB_URL = "jdbc:postgresql://" + host + ":5432/" +
            dbName +"?currentSchema=rc_racing_system_db";

    static final String USER = "dab_di20212b_100";
    static final String PASS = "Txc5x85GyM/DPALd";


    public void testUsernameTable() {

        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        }
        try {
            Connection connection =
                    DriverManager.getConnection(DB_URL, USER, PASS);

            String queryEmailsUser = " SELECT p.username, p.email"
                    + " FROM player p";

            Statement statementEmailsUser = connection.createStatement();
            ResultSet resultSetEmailsUser = statementEmailsUser.executeQuery(queryEmailsUser);

            String username = "";
            String email = "";

            while (resultSetEmailsUser.next()) {
                username = resultSetEmailsUser.getString("username");
                email = resultSetEmailsUser.getString("email");
                System.out.println(username + " - " + email);
            }
            statementEmailsUser.close();
            connection.close();

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            //return 0;
        }
    }

    /**
     * Check if the player is present in the database
     * @param email - email address obtained after authorization with oauth (after login)
     * @return
     */
    public boolean isPlayerRegistered(String email) {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        }
        try {
            Connection connection =
                    DriverManager.getConnection(DB_URL, USER, PASS);

            String queryPlayer =  " SELECT COUNT(*)"
                                + " FROM player p"
                                + " WHERE p.email = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(queryPlayer);
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            int count = 0;

            while(resultSet.next()) {
                count = resultSet.getInt(1);
            }

            preparedStatement.close();
            connection.close();

            //More than one player with the same email in db (Just in case - could be a test case)
            if (count > 1) throw new SQLException("This should't happen");

            return (count == 1);

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return false;
        }
    }

    public String getPlayerUsername(String email) {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        }
        try {
            Connection connection =
                    DriverManager.getConnection(DB_URL, USER, PASS);

            String queryPlayer =  " SELECT p.username"
                    + " FROM player p"
                    + " WHERE p.email = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(queryPlayer);
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            String username = "";

            while(resultSet.next()) {
                username = resultSet.getString(1);
            }
            preparedStatement.close();
            connection.close();

            return username;

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return null;
        }
    }

    public void insertNewPlayer(Player player) throws ClassNotFoundException, SQLException {

            Class.forName(JDBC_DRIVER);

            Connection connection =
                    DriverManager.getConnection(DB_URL, USER, PASS);

            String query =    " INSERT INTO player" +
                              " VALUES(?, ?); ";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, player.getUsername());
            preparedStatement.setString(2, player.getEmail());
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

    }
}
