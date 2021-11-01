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
     * Mathod to delete a challenge, after a rejected challenge request
     * @param challengee This user,as the challengee
     * @param id The challenge id
     */
    public static void deleteChallenge(String challenger, String challengee, int id) {
        loadDriver();
        try {
            Connection connection = getConnection();
            String deleteRequest = "DELETE FROM challenge\n" +
                    "WHERE challengeid = ? \n" +
                    "AND isfinished = false \n" +
                    "AND challengee = ? " +
                    "AND challenger = ?";

            PreparedStatement statement = connection.prepareStatement(deleteRequest);
            statement.setInt(1, id);
            statement.setString(2, challengee);
            statement.setString(3, challenger);
            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch(SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
        }

    }

    /**
     * Method to respond to a challenge.
     * @param username This user's name
     * @param id The challenge id
     */
    public static boolean respondToChallenge(String username, int id) {
        loadDriver();
        try {
            Connection connection = getConnection();
            String respond = "UPDATE challenge\n" +
                    "SET isfinished = true, challengeerace = sr.raceid\n" +
                    "FROM (SELECT r.raceid\n" +
                    "FROM race r\n" +
                    "WHERE r.player = ?\n" +
                    "ORDER BY r.raceid DESC\n" +
                    "LIMIT 1) AS sr\n" +
                    "WHERE challengeid = ?" +
                    "AND challengee = ?";

            PreparedStatement statement = connection.prepareStatement(respond);
            statement.setString(1, username);
            statement.setInt(2, id);
            statement.setString(3, username);
            statement.executeUpdate();

            statement.close();
            connection.close();

        } catch(SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return false;
        }

        return true;
    }

    /**
     * Method to check if two users are in a race
     * @param challenger
     * @param challengee
     * @return true if already in a challenge, false if otherwise
     */
    public static boolean alreadyInAChallenge (String challenger, String challengee) {
        loadDriver();
        try {
            Connection connection = getConnection();

            String challengeState = "SELECT c.challenger, c.challengeid, c.isfinished\n" +
                    "FROM challenge c\n" +
                    "WHERE (c.challenger = ?\n" +
                    "AND c.challengee = ?" +
                    "AND c.isfinished = false)\n" +
                    "OR (c.challenger = ?\n" +
                    "AND c.challengee = ?" +
                    "AND c.isfinished = false)";

            PreparedStatement statement = connection.prepareStatement(challengeState);
            statement.setString(1, challenger);
            statement.setString(2, challengee);
            statement.setString(3, challengee);
            statement.setString(4, challenger);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                statement.close();
                connection.close();
                return true;
            }

            statement.close();
            connection.close();
            return false;

        } catch(SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return false;
        }
    }

    /**
     * Method to start a new challenge.
     * If a valid id is passed, update the row. If 0 is passed as id, insert new row.
     * @param challenger This user's username
     * @param challengee This user's chalengee
     */
    public static boolean startNewChallenge(String challenger, String challengee) {
        loadDriver();

        try {
            Connection connection = getConnection();
            PreparedStatement statement = null;


            String newChallenge = "INSERT INTO challenge (challengeid, isfinished, \n" +
                        "challenger, challengee, challengerrace, challengeerace)\n" +
                        "SELECT nextval('challange_challangeid_seq'::regclass),\n" +
                        "false, r.player, ?, \n" +
                        "r.raceid, NULL\n" +
                        "FROM race r, friendship f\n" +
                        "WHERE r.player = ? \n" +
                        "ORDER BY r.raceid DESC\n" +
                        "LIMIT 1";

                statement = connection.prepareStatement(newChallenge);
                statement.setString(1, challengee);
                statement.setString(2, challenger);
                statement.execute();

                statement.close();

            connection.close();
        } catch(SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return false;
        }

        return true;
    }

    /**
     * Method to retrieve all pending/sent challenge reqests
     * @param username This user's username
     * @param pending Flag to set if we want pending or sent requests
     * @return
     */
    public static List<Challenge> getAllChallengeRequests(String username, boolean pending) {
        loadDriver();
        List<Challenge> challenges = new ArrayList<>();
        String selectStm = "";
        String whereStm = "";

        try{
            Connection connection = getConnection();

            if (pending) {
                selectStm = "c.challenger";
                whereStm = "c.challengee";

            } else {
                selectStm = "c.challengee";
                whereStm = "c.challenger";
            }

            String getRequests = "SELECT c.challengeid, " + selectStm + ",\n" +
                    " c.challengerrace \n" +
                    "FROM challenge c, race r\n" +
                    "WHERE " + whereStm + " = ?\n" +
                    "AND c.isfinished = false\n" +
                    "AND c.challengerrace = r.raceid";

            PreparedStatement statement = connection.prepareStatement(getRequests);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("challengeid");
                int challengerRace = resultSet.getInt("challengerrace");

                Challenge challenge = new Challenge(id, false, null, null, challengerRace, 0);

                if (pending) {
                    String challenger = resultSet.getString("challenger");
                    challenge.setChallenger(challenger);
                } else {
                    String challengee = resultSet.getString("challengee");
                    challenge.setChallengee(challengee);
                }

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
     * Method to get all done challenges for this user
     * @param username The username of this user
     * @return
     */
    public static List<Challenge> getAllDoneChallenges(String username) {
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
        List<Challenge> challenges = DBChallenge.getAllChallengeRequests("LiranTheDude", false);
        for (Challenge challenge: challenges) {
            System.out.println(challenge.toString());
        }

        //System.out.println(DBChallenge.checkIfFriends("KrisCross", "LoopingLaurens"));

        //DBChallenge.respondToChallenge("KaganTheMan", 26);
//        if(DBChallenge.alreadyInAChallenge("LiranTheDude", "KaganTheMan")){
//            System.out.println("In a challenge");
//        } else {
//            System.out.println("Not in a challenge");
//
//            DBChallenge.startNewChallenge("LiranTheDude", "KaganTheMan");
//        }

        //DBChallenge.respondToChallenge("KaganTheMan", 25);
        //DBChallenge.deleteChallenge("LiranTheDude", "AlexP", 25);
    }
}
