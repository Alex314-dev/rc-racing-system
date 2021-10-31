package M5Project.RC.Controller;

import M5Project.RC.Dao.PlayerDao;
import M5Project.RC.JavaClientSocket.ClientSocket;
import M5Project.RC.Resource.Database;
import M5Project.RC.Security.AfterLogin;
import M5Project.RC.model.Player;
import M5Project.RC.model.Race;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.management.DynamicMBean;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

@RestController
@CrossOrigin
public class RESTAPIController {
    //private static final String template = "Welcome %s!";


    @GetMapping("/rest/player")
    public Player player(Principal principal)
    {
        return PlayerDao.instance.getPlayer(principal.getName());
    }

    @GetMapping("/rest/myname")
    public String name(Principal principal)
    {
        return PlayerDao.instance.getPlayer(principal.getName()).getName();
    }

    @PostMapping("/rest/newplayer")
    public void newEmployee(HttpServletResponse response, @RequestParam String username, Principal principal) throws IOException {

        if (PlayerDao.instance.getPlayer(principal.getName()).getUsername().equals("")) {

            try {
                if (username.matches("\\b[a-zA-Z][a-zA-Z0-9\\-._]{3,}\\b")) {
                    Database db = new Database();
                    Player newPlayer = PlayerDao.instance.getPlayer(principal.getName());
                    try {
                        newPlayer.setUsername(username);
                        db.insertNewPlayer(newPlayer);
                        response.sendRedirect("/race");
                    } catch (ClassNotFoundException e) {
                        response.sendRedirect("/newuser?error=wrong");
                        newPlayer.setUsername("");
                        e.printStackTrace();
                    } catch (SQLException throwables) {
                        response.sendRedirect("/newuser?error=exists");
                        newPlayer.setUsername("");
                        System.out.println("sqlExept");
                        throwables.printStackTrace();
                    }
                } else {
                    response.sendRedirect("/newuser?error=illegal ");
                }
            } catch (PatternSyntaxException ex) {
                response.sendRedirect("/newuser?error=wrong");
                ex.printStackTrace();
            }
        } else {
            response.sendRedirect("/race");
        }

    }

    @GetMapping("/rest/allraces")
    public List<Race> allRaces() {
        return Database.getRacesByUser(null);
    }

    @GetMapping("/rest/myraces")
    public List<Race> myRaces(Principal principal) {
        return Database.getRacesByUser(PlayerDao.instance.getPlayer(principal.getName()).getUsername());
    }

    @GetMapping("/rest/timer")
    public boolean timer(Principal principal) {
        if (!ClientSocket.instance.isOngoingGame()) {
            return false; // there is no race going on
        }

        String username = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        if (ClientSocket.instance.getCurrentRacer().equals(username)) {
            return ClientSocket.instance.isRaceStarted();
        } else {
            return false; // you are not the one racing ztupid
        }
    }

    @GetMapping("/rest/race")
    public float normalRace(Principal principal) {
        if (ClientSocket.instance.isOngoingGame()) {
            return -2;
        }

        ClientSocket.instance.setOngoingGame(true);
        String username = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        ClientSocket.instance.setCurrentRacer(username);

        String result = "";
        try {
            result = ClientSocket.instance.startRace();
        } catch (Exception e) {
            ClientSocket.instance.setCurrentRacer("");
            ClientSocket.instance.setOngoingGame(false);
            e.printStackTrace();
            return -1;
        }

        if (result.contains("Invalid")) {
            ClientSocket.instance.setCurrentRacer("");
            ClientSocket.instance.setOngoingGame(false);
            return -1;
        }

        String[] resultStrArr = result.split("~");
        List<Float> times = new ArrayList<Float>();
        for (String r : resultStrArr) {
            times.add(Float.parseFloat(r));
        }
        float overallTime = times.get(times.size() - 1);
        times.remove(times.size() - 1);

        Database.addNewRace(username, overallTime, times);
        ClientSocket.instance.setCurrentRacer("");
        ClientSocket.instance.setOngoingGame(false);
        return overallTime;
    }
}
