package M5Project.RC.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import M5Project.RC.Resource.Database;
import M5Project.RC.model.Race;
import M5Project.RC.model.Player;


public class DatabaseTest {

    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String host = "bronto.ewi.utwente.nl";
    static final String dbName = "dab_di20212b_100";
    static final String DB_URL = "jdbc:postgresql://" + host + ":5432/" +
            dbName +"?currentSchema=rc_racing_system_db";

    static final String USER = "dab_di20212b_100";
    static final String PASS = System.getenv("RC_DB_PASS");

    static final int NUM_RACES = 23;
    static final int NUM_USERS = 15;
    static final String RACE_USER = "AlexP";


    /*
     * Test if we get all races that are currently in the database
     */
    @Test
    void checkAllRaces(){
        List<Race> races = Database.getRacesByUser(null);
        int actualRaces = races.size();

        assertEquals(NUM_RACES, actualRaces);
    }

    /*
     * Test if we get all races that are currently in the database for a specific user
     */
    @Test
    void checkAllRacesUser(){
        List<Race> races = Database.getRacesByUser(RACE_USER);
        int actualRaces = races.size();
        assertEquals(RACE_USER, races.get(0).getPlayer());
        assertEquals(RACE_USER, races.get(1).getPlayer());
        assertEquals(RACE_USER, races.get(2).getPlayer());

        assertEquals(3, actualRaces);
    }

    /*
     * Test if we get 0 races for a user that does not exist
     */
    @Test
    void checkAllRacesNoUser(){
        List<Race> races = Database.getRacesByUser("TestUser");
        int actualRaces = races.size();

        assertEquals(0, actualRaces);
    }

    /*
     * Test inserting a new race.
     */
    @Test
    void checkInsertNewRace(){
        float raceTime = 8000;
        List<Float> sectorTimes = new ArrayList<>();
        sectorTimes.add(9000f);
        sectorTimes.add(10000f);
        sectorTimes.add(11000f);

        Database.addNewRace(RACE_USER, raceTime, sectorTimes);
        List<Race> races = Database.getRacesByUser(null); //getting all races

        assertEquals(NUM_RACES + 1, races.size());

        deleteNewRace(); //remove the test race

    }

    /*
     * Test getting a username from email
     */
    @Test
    void checkUsernameFromEmail(){
        String email = "AlexP@email.com";
        String username = Database.getPlayerUsername(email);

        assertEquals(RACE_USER, username);

    }

    /*
     * Test if we get all present usernames
     */
    @Test
    void checkAllUsernames() {
        List<String> usernames = Database.getAllUsernames();

        assertEquals(NUM_USERS, usernames.size());
    }

    /*
     * Test if a player who logs in is already in the database or not
     */
    @Test
    void checkPlayerRegistered() {
        assertTrue(Database.isPlayerRegistered("AlexP@email.com"));
        assertFalse(Database.isPlayerRegistered(("new.email@email.com")));
    }

    //TODO check insert existing user
    /*
     * Test inserting a new user
     */
    @Test
    void checkInsertNewUser() throws SQLException, ClassNotFoundException {
        Player newPlayer = new Player("NewPlayer", "new.email@email.com", null);
        assertTrue(Database.insertNewPlayer(newPlayer));

        List<String> usernames = Database.getAllUsernames(); //get all players
        assertEquals(NUM_USERS+1, usernames.size());

        deleteNewPlayer();
    }

    /*
     * Test inserting a new user whose mail already exists
     */
    @Test
    void checkInsertNewExistingUser() throws SQLException, ClassNotFoundException {
        Player newPlayer = new Player("KaganTheMan", "kagantheman@mail.com", null);
        assertFalse(Database.insertNewPlayer(newPlayer));

        List<String> usernames = Database.getAllUsernames(); //get all players
        assertEquals(NUM_USERS, usernames.size());
    }

    private static void deleteNewRace() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        }

        try{
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            int raceId = 0;

            String latestIdQuery = "SELECT r.raceid\n" +
                    "FROM race r\n" +
                    "ORDER BY r.raceid DESC\n" +
                    "LIMIT 1";

            Statement statement  = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(latestIdQuery);

            while(resultSet.next()){
                raceId = resultSet.getInt("raceid");
            }
            statement.close();

            String deleteQuery = "DELETE FROM race WHERE raceid = " + raceId;

            Statement statement1  = connection.createStatement();
            statement1.execute(deleteQuery);
            statement1.close();

            connection.close();

        } catch(SQLException sqle){
            System.err.println("Error connecting: " + sqle);
        }

    }

    private static void deleteNewPlayer() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        }

        try{
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

            String deleteQuery = "DELETE FROM player WHERE username = 'NewPlayer'";

            Statement statement1  = connection.createStatement();
            statement1.execute(deleteQuery);
            statement1.close();

            connection.close();

        } catch(SQLException sqle){
            System.err.println("Error connecting: " + sqle);
        }

    }
}
