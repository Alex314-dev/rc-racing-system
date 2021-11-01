package M5Project.RC.Resource;

import M5Project.RC.model.Challenge;

import javax.swing.plaf.nimbus.State;
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
     * Method to respond to a challenge.
     * @param username This user's name
     * @param id The challenge id
     */
    public static void respondToChallenge(String username, int id) {
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
                    "WHERE challengeid = ?";

            PreparedStatement statement = connection.prepareStatement(respond);
            statement.setString(1, username);
            statement.setInt(2, id);
            statement.executeUpdate();

            statement.close();
            connection.close();

        } catch(SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
        }
    }

    /**
     * Method to start a new challenge. If the challenge row already exists, return boolean is false.
     * If a successful new challenge is started for existing row/inserted for new row, return boolean is true.
     * @param challenger This user's username
     * @param challengee This user's chalengee
     * @return false if challenge already in progress, true if successful new challenge.
     */
    public static boolean startNewChallenge(String challenger, String challengee) {
        loadDriver();
        boolean newRow = true;
        boolean creationFlag = true;

        try {
            Connection connection = getConnection();
            int challengeid = 0;

            String newChallenge = "";

            String challengeState = "SELECT c.challenger, c.challengeid, c.isfinished\n" +
                    "FROM challenge c\n" +
                    "WHERE (c.challenger = ?\n" +
                    "AND c.challengee = ?)\n" +
                    "OR (c.challenger = ?\n" +
                    "AND c.challengee = ?)";

            PreparedStatement statement = connection.prepareStatement(challengeState);
            statement.setString(1, challenger);
            statement.setString(2, challengee);
            statement.setString(3, challengee);
            statement.setString(4, challenger);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getBoolean("isfinished") == true) {
                    challengeid = resultSet.getInt("challengeid");

                    newChallenge = "UPDATE challenge\n" +
                            "SET isfinished = false, challengerrace = sr.raceid\n" +
                            "FROM (SELECT r.raceid\n" +
                            "FROM race r\n" +
                            "WHERE r.player = ?\n" +
                            "ORDER BY r.raceid DESC\n" +
                            "LIMIT 1) AS sr " +
                            "WHERE challengeid = " + challengeid +"\n"; //no need for sanitazation as we get the data from here
                    statement = connection.prepareStatement(newChallenge);
                    statement.setString(1, challenger);
                    statement.execute();

                    System.out.println("Row exists, starting new challenge");
                    newRow = false;

                } else if (resultSet.getBoolean("isfinished") == false) {
                    newRow = false;
                    creationFlag = false;

                    System.out.println("Ongoing challenge");

                } else { //insert new challenge row
                    System.out.println("HERE");

                    System.out.println("Successfully inserted new challenge");
                }
            }

            if (newRow) {
                newChallenge = "INSERT INTO challenge (challengeid, isfinished, \n" +
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
            }

            connection.close();
            statement.close();
            return creationFlag;
        } catch(SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return creationFlag;
        }
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
//        List<Challenge> challenges = DBChallenge.getAllChallengeRequests("AlexP", true);
//        for (Challenge challenge: challenges) {
//            System.out.println(challenge.toString());
//        }
        //System.out.println(DBChallenge.startNewChallenge("LiranTheDude", "KaganTheMan"));

        //System.out.println(DBChallenge.checkIfFriends("KrisCross", "LoopingLaurens"));

        DBChallenge.respondToChallenge("KaganTheMan", 26);
    }
}
