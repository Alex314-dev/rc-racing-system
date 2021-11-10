package M5Project.RC.Resource;

import M5Project.RC.model.Player;
import M5Project.RC.model.Race;
import org.springframework.security.core.parameters.P;

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
     * @return  the requestSuccessFlag: -1 if SQL error, 0 if successful friend request,
     * 1 if already friends, 2 if already in request, 3 if that user does not exist
     */
    public static int sendFriendRequest(String username, String friend) {
        if (username.equals(friend)) { return -1; }

        if (!DBRacePlayer.getAllUsernames().contains(friend)) { return 3; }

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

                requestSuccessFlag =  0;
            } else if (isFriends == false){
                requestSuccessFlag =  2;
            } else if (isOngoingRequest == false){
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

    /**
     * Respond to a request. AKA change the valid flag to true for the given players
     * @param friend1
     * @param friend2
     * @return -1 if error, 0 if successful update
     */
    public static int respondToRequest(String friend1, String friend2) {
        loadDriver();
        try {
            Connection connection = getConnection();

            String updateRequest = "UPDATE friendship\n" +
                    "SET valid = true\n" +
                    "WHERE (friend1 = ?\n" +
                    "AND friend2 = ?)" +
                    "OR (friend1 = ?" +
                    "AND friend2 = ?)";
            PreparedStatement statement = connection.prepareStatement(updateRequest);
            statement.setString(1, friend1);
            statement.setString(2, friend2);
            statement.setString(3, friend2);
            statement.setString(4, friend1);
            int flag = statement.executeUpdate();

            statement.close();
            connection.close();

            if (flag != 0) {
                return 0;
            }
            return -1;
        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return -1;
        }
    }

    /**
     * Method to get all friends requests. If pending boolean is set,
     * then return all pending requests, if not, return all outgoing requests.
     * @param username the username of the current user
     * @param pending the pending flag (decides output)
     * @return null in case of SQL error, otherwise the list of friends (as Strings)
     */
    public static List<String> getRequests(String username, boolean pending) {
        loadDriver();
        List<String> friends = new ArrayList<>();
        String requests = "";
        String friend = "";

        if (pending) {
            requests = "SELECT f.friend1\n" +
                    "FROM friendship f\n" +
                    "WHERE f.friend2 = ?\n" +
                    "AND f.valid = false";
            friend = "friend1";
        } else {
            requests = "SELECT f.friend2\n" +
                    "FROM friendship f\n" +
                    "WHERE f.friend1 = ?\n" +
                    "AND f.valid = false";
            friend = "friend2";
        }

        try {
            Connection connection = getConnection();

            PreparedStatement statement = connection.prepareStatement(requests);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                friends.add(resultSet.getString(friend));
            }

            statement.close();
            connection.close();
            return friends;
        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return null;
        }
    }

    /**
     * Method to delete a friendship between the current player and a specified user
     * @param friend friend to unfriend
     * @return 0 in case of SQL error, 1 if delete was completed successfully
     */
    public static int deleteFriend(String username, String friend) {
        loadDriver();
        try {
            Connection connection = getConnection();

            String deleteFriendship = "DELETE FROM friendship\n" +
                    "WHERE (friend1 = ?\n" +
                    "AND friend2 = ?)\n" +
                    "OR (\n" +
                    "friend1 = ?\n" +
                    "AND friend2 = ?)";

            PreparedStatement statement = connection.prepareStatement(deleteFriendship);
            statement.setString(1, username);
            statement.setString(2, friend);
            statement.setString(3, friend);
            statement.setString(4, username);
            statement.execute();

            statement.close();
            connection.close();

            DBChallenge.deleteALlChallengesUsers(username, friend);
        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return -1;
        }
        return 0;
    }

    /**
     * Method to return the current friends of a user with their wins and losses
     * @param username this player username
     * @return a list of players with wins and losses (friends)
     */
    public static List<Player> getFriendsWinsLosses(String username) {
        loadDriver();
        List<Player> friends = new ArrayList<>();
        try {
            Connection connection = getConnection();
            String friendsQuery = "SELECT p.username as friends\n" +
                    "FROM player p, friendship f\n" +
                    "WHERE (f.friend1 = ?\n" +
                    "AND f.friend2 = p.username " +
                    "AND f.valid = true)\n" +
                    "OR (f.friend2 = ?\n" +
                    "AND f.friend1 = p.username " +
                    "AND f.valid = true)\n";

            PreparedStatement statement = connection.prepareStatement(friendsQuery);
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                String friendUsername = resultSet.getString("friends");
                Player newFriend = new Player(friendUsername, 0, 0);
                friends.add(newFriend);
            }

            //win, loss
            String friendsWins1 = "SELECT jsonb_agg(json_build_object('win', f.friend2win, 'loss', f.friend1win) ) as score, " +
                    "f.friend2win as wins, f.friend1win as losses\n" +
                    "FROM friendship f\n" +
                    "WHERE f.friend1 = ?\n" +
                    "AND f.friend2 = ?" +
                    "GROUP BY wins, losses";

            //win, loss
            String friendsWins2 = "SELECT f.friend1win as wins, f.friend2win as losses\n" +
                    "FROM friendship f\n" +
                    "WHERE f.friend2 = ?\n" +
                    "AND f.friend1 = ?";


            for (Player friend: friends) {
                statement = connection.prepareStatement(friendsWins1);
                statement.setString(1, username);
                statement.setString(2, friend.getUsername());
                ResultSet resultSet1 = statement.executeQuery();

                int wins = 0;
                int losses = 0;
                boolean otherQuery = true;

                while (resultSet1.next()) {
                    String check = resultSet1.getString("score");
                    if (check != null) {
                        wins = resultSet1.getInt("wins");
                        losses = resultSet1.getInt("losses");
                        otherQuery = false;
                    }
                }

                if (otherQuery) {
                    statement = connection.prepareStatement(friendsWins2);
                    statement.setString(1, username);
                    statement.setString(2, friend.getUsername());
                    ResultSet resultSet2 = statement.executeQuery();

                    while (resultSet2.next()) {
                        wins = resultSet2.getInt("wins");
                        losses = resultSet2.getInt("losses");
                    }
                }

                friend.setWins(wins);
                friend.setLosses(losses);
            }

            statement.close();
            connection.close();

            return friends;
        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return null;
        }
    }

    /**
     * Helper function to load the driver
     */
    private static void loadDriver() {
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
