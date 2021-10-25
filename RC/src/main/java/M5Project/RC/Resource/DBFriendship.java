package M5Project.RC.Resource;

import M5Project.RC.model.Player;
import M5Project.RC.model.Race;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBFriendship {
    //information for methods to access database
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String host = "bronto.ewi.utwente.nl";
    static final String dbName = "dab_di20212b_100";
    static final String DB_URL = "jdbc:postgresql://" + host + ":5432/" +
            dbName +"?currentSchema=rc_racing_system_db";

    static final String USER = "dab_di20212b_100";
    static final String PASS = System.getenv("RC_DB_PASS");

    /**
     * Send a friend reqeust. AKA create a new row in the friends table with valid false and scores 0.
     * @param username the current user
     * @param friend desired friend
     * @return  the requestSuccessFlag: -1 if SQL error, 0 if successful friend request, 1 if already friends, 2 if already in request
     */
    public static int sendFriendRequest(String username, String friend){
        loadDriver();
        boolean isFriends = false;
        boolean isOngoingRequest = false;
        int requestSuccessFlag = -1;

        try{
            Connection connection = getConnection();

            String isQuery = "SELECT f.friend1\n" +
                    "FROM friendship f\n" +
                    "WHERE (f.friend1 = ?\n" +
                    "AND f.friend2 = ?" +
                    "AND f.valid = ?)\n" +
                    "OR (f.friend2 = ?\n" +
                    "AND f.friend1 = ?" +
                    "AND f.valid = ?)";

            PreparedStatement prStatement = connection.prepareStatement(isQuery);
            prStatement.setString(1, username);
            prStatement.setString(2, friend);
            prStatement.setBoolean(3, true);
            prStatement.setString(4, username);
            prStatement.setString(5, friend);
            prStatement.setBoolean(6, true);
            ResultSet resultSet = prStatement.executeQuery();

            while (resultSet.next()) {
                isFriends = resultSet.getString("friend1") == null ? false : true;
            }


            prStatement = connection.prepareStatement(isQuery);
            prStatement.setString(1, username);
            prStatement.setString(2, friend);
            prStatement.setBoolean(3, false);
            prStatement.setString(4, username);
            prStatement.setString(5, friend);
            prStatement.setBoolean(6, false);
            ResultSet resultSet1 = prStatement.executeQuery();


            while (resultSet1.next()) {

                isOngoingRequest = resultSet1.getString("friend1") == null ? false : true;

            }

            if (isFriends == false && isOngoingRequest == false) {
                String newRequestQuery = "INSERT INTO friendship(friend1, friend2, valid, friend1win, friend2win)\n" +
                        "VALUES (?, ?, false, 0,  0)";

                prStatement = connection.prepareStatement(newRequestQuery);
                prStatement.setString(1, username);
                prStatement.setString(2, friend);
                prStatement.executeUpdate();

                System.out.println("Request sent");

                requestSuccessFlag =  0;
            } else if (isFriends == false){
                System.out.println("Already requested");
                requestSuccessFlag =  2;
            } else if (isOngoingRequest == false){
                System.out.println("Already friends");
                requestSuccessFlag = 1;
            }

            prStatement.close();
            connection.close();
            return requestSuccessFlag;
        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return requestSuccessFlag;
        }
    }

    public static List<String> getAllFriendsUsernames() {
        //TODO
        List<String> usernames = new ArrayList<>();
        return usernames;
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

    public static void main(String[] args) {
        DBFriendship.sendFriendRequest("LoopingLaurens", "LiranTheDude");
    }
}