package M5Project.RC.Resource;

import M5Project.RC.model.Challenge;

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
                    "AND f.friend2 = ?" +
                    " AND f.valid = true)\n" +
                    "OR (f.friend2 = ?\n" +
                    "AND f.friend1 = ?" +
                    " AND f.valid = true)\n";
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

            connection.close();
            statement.close();
            return checkFriends;

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return false;
        }
    }

    /**
     * Method to get all done challenges for this user
     * @param username The username of this user
     * @return
     */
    public static List<Challenge> getAllDoneChallenges (String username) {
        loadDriver();
        List<Challenge> challenges = new ArrayList<>();

        try {
            Connection connection = getConnection();

            String getChallenges = "SELECT c.challengeid, c.challenger,\n" +
                    " c.challengerrace, c.challengee,\n" +
                    " c.challengeerace\n" +
                    "FROM challenge c\n" +
                    "WHERE (c.challenger = ?" +
                    " AND c.isfinished = true)\n" +
                    " OR (c.challengee = ?" +
                    " AND c.isfinished = true)\n";

            PreparedStatement statement = connection.prepareStatement(getChallenges);
            statement.setString(1, username);
            statement.setString(2, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("challengeid");
                String challenger = resultSet.getString("challenger");
                String challengee = resultSet.getString("challengee");
                int challengerRace = resultSet.getInt("challengerrace");
                int challengeeRace = resultSet.getInt("challengeerace");

                Challenge challenge = new Challenge(id, true, challenger, challengee, challengerRace, challengeeRace);
                challenges.add(challenge);
            }

            connection.close();
            statement.close();
            return challenges;

        } catch (SQLException sqle) {
                System.err.println("Error connecting: " + sqle);
                return null;
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

    public static void main(String[] args) {
        List<Challenge> challenges = DBChallenge.getAllDoneChallenges("KrisCross");
        for (Challenge challenge: challenges) {
            System.out.println(challenge.toString());
        }

        System.out.println(DBChallenge.checkIfFriends("KrisCross", "LoopingLaurens"));
    }
}
