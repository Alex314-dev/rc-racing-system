package M5Project.RC.Resource;

import M5Project.RC.model.Player;
import M5Project.RC.model.Race;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBChallenge {
    //information for methods to access database
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String host = "bronto.ewi.utwente.nl";
    static final String dbName = "dab_di20212b_100";
    static final String DB_URL = "jdbc:postgresql://" + host + ":5432/" +
            dbName +"?currentSchema=rc_racing_system_db";

    static final String USER = "dab_di20212b_100";
    static final String PASS = System.getenv("RC_DB_PASS");


    /**
     * Method to check if the chalengee and challenger are friends
     * @param username This user's username
     * @param chalengee The chalengee
     * @return
     */
    public static boolean checkIfFriends(String username, String chalengee){
        loadDriver();
        boolean checkFriends = false;

        try {
            Connection connection = getConnection();

            String checkFriend = "SELECT f.friend1\n" +
                    "FROM friendship f\n" +
                    "WHERE (f.friend1 = ?\n" +
                    "AND f.friend2 = ?)\n" +
                    "OR (f.friend2 = ?\n" +
                    "AND f.friend1 = ?)\n" +
                    "AND f.valid = true;";
            PreparedStatement statement = connection.prepareStatement(checkFriend);
            statement.setString(1, username);
            statement.setString(2, chalengee);
            statement.setString(3, username);
            statement.setString(4, chalengee);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                String check = resultSet.getString("friend1");
                if (check != null) {
                    checkFriends = true;
                }
            }

            return checkFriends;

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return false;
        }
    }

    public static List<Challenge> getAllDoneChallenges (String challenger, String challengee) {
        loadDriver();
        boolean checkFriends = false;

        try {
            Connection connection = getConnection();


        }catch (SQLException sqle) {
                System.err.println("Error connecting: " + sqle);
                return false;
        }
    }


    /**
     * Helper function to load the driver
     */
    private static void loadDriver(){
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        }
    }

    /**
     * Helper function to establish connection with DB server
     * @return the Connection object
     * @throws SQLException
     */
    private static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        return connection;
    }
}
