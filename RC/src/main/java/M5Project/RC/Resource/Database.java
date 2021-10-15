package M5Project.RC.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
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
    static final String PASS = "Txc5x85GyM/DPALd";


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

            while(resultSetEmailsUser.next()) {
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


    public static void main(String[] args) {

        Database db = new Database();
        db.testUsernameTable();

    }


}
