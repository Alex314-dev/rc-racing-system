package M5Project.RC.Resource;

import M5Project.RC.model.ErrorMessage;
import M5Project.RC.model.Player;
import M5Project.RC.model.Race;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBRacePlayer {
    //information for methods to access database
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String host = "bronto.ewi.utwente.nl";
    static final String dbName = "dab_di20212b_100";
    static final String DB_URL = "jdbc:postgresql://" + host + ":5432/" +
            dbName +"?currentSchema=rc_racing_system_db";

    static final String USER = "dab_di20212b_100";
    static final String PASS = System.getenv("RC_DB_PASS");

    /**
     * Method to delete all data for the specified user.
     * @param username The username of the account
     * @return #affected rows if the deletion was executed correctly, ErrorMessage.SERVER_ERROR if not
     */
    public static int deleteAccount(String username) {
        loadDriver();

        try {
            Connection connection = getConnection();
            String deleteAccount = "DELETE FROM player\n" +
                    "WHERE username = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(deleteAccount);
            preparedStatement.setString(1, username);

            int result = preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
            return result;

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return ErrorMessage.SERVER_ERROR;
        }
    }

    /**
     * Check if the player is present in the database
     * @param email - email address obtained after authorization with oauth (after login)
     * @return
     */
    public static boolean isPlayerRegistered(String email) {
        loadDriver();

        try {
            Connection connection = getConnection();

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

    /**
     * Method to get a player username from email
     * @param email
     * @return
     */
    public static String getPlayerUsername(String email) {
        loadDriver();

        try {
            Connection connection = getConnection();

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

    /**
     * Method to insert a new player
     * @param player
     * @return false if the insert was unsuccessful if that user already exists, true if successful
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static boolean insertNewPlayer(Player player) throws ClassNotFoundException, SQLException {
        loadDriver();

        String email = player.getEmail();
        String username = player.getUsername();
        boolean flag = false;
        Connection connection =
                DriverManager.getConnection(DB_URL, USER, PASS);
        String emailEmailQuery = "SELECT COUNT(p.username) " +
                "FROM player p " +
                "WHERE p.email = ?";


        String query =    " INSERT INTO player" +
                " VALUES(?, ?); ";

        PreparedStatement preparedStatement = connection.prepareStatement(emailEmailQuery);
        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();

        while(resultSet.next()){
            if (resultSet.getInt(1) == 1){
                flag = false;
            } else {
                flag = true;
            }
        }

        if (flag) {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.executeUpdate();
        }
        preparedStatement.close();
        connection.close();
        return flag;
    }

    /**
     * Getting all race by a username. If username is null, give all races.
     * @param username username of player
     * @return
     */
    public static List<Race> getRacesByUser(String username) {
        loadDriver();
        String usernameOption = "";
        String playerTable = "";

        if (username != null) {
            usernameOption = " p.username = r.player\n AND p.username = ?\n AND ";
            playerTable = ", player p\n";
        }

        try {
            Connection connection = getConnection();

            String queryRace =  "SELECT r.player, r.raceid, r.datetime, r.overallTime, s1.time as s1, s2.time as s2,\n" +
                    "s3.time as s3\n" +
                    "FROM race r, sector s1, sector s2, sector s3" + playerTable +
                    " WHERE" +
                    usernameOption +
                    " s1.raceid = r.raceid\n" +
                    "AND s2.raceid = r.raceid\n" +
                    "AND s3.raceid = r.raceid\n" +
                    "AND s1.secnum = 1\n" +
                    "AND s2.secnum = 2\n" +
                    "AND s3.secnum = 3\n" +
                    "ORDER BY r.overallTime, r.datetime ASC";

            PreparedStatement preparedStatement = connection.prepareStatement(queryRace);

            if (username != null) {
                preparedStatement.setString(1, username);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Race> races = new ArrayList<>();

            while(resultSet.next()) {
                List<Float> sectorTimes = getSectorTimes( resultSet.getFloat("s1"),
                        resultSet.getFloat("s2"),
                        resultSet.getFloat("s3"));

                Race race = new Race(resultSet.getInt("raceid"),
                        resultSet.getString("player"),
                        resultSet.getTimestamp("datetime"),
                        resultSet.getFloat("overallTime"),
                        sectorTimes);

                races.add(race);
            }
            preparedStatement.close();
            connection.close();

            return races;

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return null;
        }

    }

    /**
     * Method to insert a new race into the database. After making a new race, we
     * get the newest race id and add the new sector times for that race.
     * @param username - the player who is logged in and made a race
     * @param raceTime - the overall time of the race
     */
    public static void addNewRace(String username, Float raceTime, List<Float> sectorTimes) {
        loadDriver();
        int raceId = 0;

        try {
            Connection connection = getConnection();

            String addRaceQuery = "INSERT INTO race (raceid, datetime, player, overalltime)\n" +
                    "VALUES (nextval('SEQ_ID'), LOCALTIMESTAMP, " +
                    "?, ?)";
            PreparedStatement prStatement = connection.prepareStatement(addRaceQuery);
            prStatement.setString(1, username);
            prStatement.setFloat(2, raceTime);
            prStatement.execute();

            String latestIdQuery = "SELECT r.raceid\n" +
                    "FROM race r\n" +
                    "ORDER BY r.raceid DESC\n" +
                    "LIMIT 1";

            Statement statement  = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(latestIdQuery);

            while(resultSet.next()){
                raceId = resultSet.getInt("raceid");
            }

            String newSectorsQuery = "INSERT INTO sector(secnum, raceid, time)\n" +
                    "VALUES (?, ?, ?)";
            prStatement = connection.prepareStatement(newSectorsQuery);

            for (int i = 0; i < 3; i++) {
                prStatement.setInt(1, i+1);
                prStatement.setInt(2, raceId);
                prStatement.setFloat(3, sectorTimes.get(i));
                prStatement.execute();
            }

            statement.close();
            prStatement.close();
            connection.close();

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
        }
    }

    /**
     * Method to get all existing usernames in a list.
     * @return
     */
    public static List<String> getAllUsernames(){
        loadDriver();
        List<String> usernames = new ArrayList<>();
        try{
            Connection connection = getConnection();

            String usernameQuery = "SELECT p.username\n" +
                    "FROM player p";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(usernameQuery);

            while (resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }
            statement.close();
            connection.close();

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
            return null;
        }
        return usernames;
    }

    /**
     * Helper function to add all sector times in a list
     * @param sector1
     * @param sector2
     * @param sector3
     * @return
     */
    private static List<Float> getSectorTimes(float sector1, float sector2, float sector3) {
        List<Float> sectorTime = new ArrayList<>();
        sectorTime.add(sector1);
        sectorTime.add(sector2);
        sectorTime.add(sector3);
        return sectorTime;
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
