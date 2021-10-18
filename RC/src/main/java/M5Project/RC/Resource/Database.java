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
        loadDriver();

        try {
            Connection connection = getConnection();

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

    public String getPlayerUsername(String email) {
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

    public void insertNewPlayer(Player player) {
        loadDriver();
        try {
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
        } catch (SQLException sqle) {
            System.err.println("Error connecting: " + sqle);
        }

    }

    public static List<Race> getRacesByUser(String username) {
        loadDriver();

        try {
            Connection connection = getConnection();

            String queryPlayer =  "SELECT r.raceid, r.datetime, r.overallTime, s1.time, s2.time,\n" +
                    "s3.time\n" +
                    "FROM race r, sector s1, sector s2, sector s3, sector s4, player p\n" +
                    "WHERE p.username = ?" +
                    "AND p.username = r.player\n" +
                    "AND s1.raceid = r.raceid\n" +
                    "AND s2.raceid = r.raceid\n" +
                    "AND s3.raceid = r.raceid\n" +
                    "AND s4.raceid = r.raceid\n" +
                    "AND s1.secnum = 1\n" +
                    "AND s2.secnum = 2\n" +
                    "AND s3.secnum = 3\n" +
                    "AND s4.secnum = 4";

            PreparedStatement preparedStatement = connection.prepareStatement(queryPlayer);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Race> races = new ArrayList<>();

            while(resultSet.next()) {
                Race race = new Race(resultSet.getInt("raceid"),
                        username,
                        resultSet.getTimestamp("datetime"),
                        resultSet.getTime("overallTime"),
                        null);

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

        List<Race> races = RaceDao.instance.getRaces("AlexP");

        for (Race race: races) {
            System.out.println(race.toString());
        }

    }
}
