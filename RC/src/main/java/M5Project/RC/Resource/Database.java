package M5Project.RC.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import M5Project.RC.Dao.RaceDao;
import M5Project.RC.model.Player;
import M5Project.RC.model.Race;
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
    static final String PASS = System.getenv("RC_DB_PASS");


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

    public void insertNewPlayer(Player player) throws ClassNotFoundException, SQLException{
        loadDriver();

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

    public static List<Race> getRacesByUser(String username) {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        }
        try {
            Connection connection =
                    DriverManager.getConnection(DB_URL, USER, PASS);

            String queryRace =  "SELECT r.raceid, r.datetime, r.overallTime, s1.time as s1, s2.time as s2,\n" +
                    "s3.time as s3\n" +
                    "FROM race r, sector s1, sector s2, sector s3, player p\n" +
                    "WHERE p.username = ?" +
                    "AND p.username = r.player\n" +
                    "AND s1.raceid = r.raceid\n" +
                    "AND s2.raceid = r.raceid\n" +
                    "AND s3.raceid = r.raceid\n" +
                    "AND s1.secnum = 1\n" +
                    "AND s2.secnum = 2\n" +
                    "AND s3.secnum = 3\n";

            PreparedStatement preparedStatement = connection.prepareStatement(queryRace);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Race> races = new ArrayList<>();


            while(resultSet.next()) {
                List<Time> sectorTimes = getSectorTimes( resultSet.getTime("s1"),
                        resultSet.getTime("s2"),
                        resultSet.getTime("s3"));

                Race race = new Race(resultSet.getInt("raceid"),
                        username,
                        resultSet.getTimestamp("datetime"),
                        resultSet.getTime("overallTime"),
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
    public static  void addNewRace(String username, Time raceTime, List<Time> sectorTimes) {
        loadDriver();
        int raceId = 0;

        try {
            Connection connection = getConnection();

            String addRaceQuery = "INSERT INTO race (raceid, datetime, player, overalltime)\n" +
                    "VALUES (nextval('race_raceid_seq'::regclass), LOCALTIMESTAMP, " +
                    "?, ?)";
            PreparedStatement prStatement = connection.prepareStatement(addRaceQuery);
            prStatement.setString(1, username);
            prStatement.setTime(2, raceTime);
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
                prStatement.setTime(3, sectorTimes.get(i));
                prStatement.execute();
            }

            statement.close();
            prStatement.close();
            connection.close();

        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
        }
    }
    private static List<Time> getSectorTimes(Time sector1, Time sector2, Time sector3) {
        List<Time> sectorTime = new ArrayList<>();
        sectorTime.add(sector1);
        sectorTime.add(sector2);
        sectorTime.add(sector3);

        return sectorTime;
    }

    private static void loadDriver(){
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        }
    }

    private static Connection getConnection() throws SQLException{
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        return connection;
    }

    public static void main(String args[]) {
//        List<Time> sectorTimes = new ArrayList<>();
//        sectorTimes.add(new Time(6000));
//        sectorTimes.add(new Time(7000));
//        sectorTimes.add(new Time(8000));
//
//        for (Time time: sectorTimes) {
//            System.out.println("Time: " + time);
//        }
//
//
//
//        Database.addNewRace("AlexP", new Time(21000), sectorTimes);

        List<Race> races = RaceDao.instance.getRaces("Pirzan");

        for (Race race: races) {
            System.out.println(race.toString());
        }


    }
}
