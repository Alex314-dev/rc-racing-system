package M5Project.RC.Controller;

import M5Project.RC.Dao.ChallengeDao;
import M5Project.RC.Dao.PlayerDao;
import M5Project.RC.Dao.RaceDao;
import M5Project.RC.JavaClientSocket.ClientSocket;
import M5Project.RC.Resource.DBChallenge;
import M5Project.RC.model.Challenge;
import M5Project.RC.model.Player;
import M5Project.RC.model.Race;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
                    Player newPlayer = PlayerDao.instance.getPlayer(principal.getName());
                    try {
                        newPlayer.setUsername(username);
                        PlayerDao.instance.addPlayerToDB(newPlayer);
                        response.sendRedirect("/race");
                    } catch (ClassNotFoundException e) {
                        response.sendRedirect("/newuser?error=wrong");
                        newPlayer.setUsername("");
                        e.printStackTrace();
                    } catch (SQLException throwables) {
                        response.sendRedirect("/newuser?error=exists");
                        newPlayer.setUsername("");
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
        return RaceDao.instance.getRaces(null);
    }

    @GetMapping("/rest/myraces")
    public List<Race> myRaces(Principal principal) {
        return RaceDao.instance.getRaces(PlayerDao.instance.getPlayer(principal.getName()).getUsername());
    }

    @GetMapping("/rest/race")
    public float normalRace(Principal principal) {
        return RaceDao.instance.initiateARace(principal);
    }

    @PostMapping("/rest/challengeRequest")
    public float challengeRequest(@RequestParam String challengee, Principal principal) {
        String challenger = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        if (ChallengeDao.instance.challengeRequest(challenger, challengee)) {
            float overallTime = RaceDao.instance.initiateARace(principal);
            if (overallTime > 0) {
                if (DBChallenge.startNewChallenge(challenger, challengee)) {
                    return overallTime;
                }
                return -1;
            }
            // TODO: decide if we are going to change the scores here
            return overallTime;
        }
        return -1;
    }

    @PostMapping("/rest/acceptChallenge")
    public float acceptChallenge(@RequestParam int id, Principal principal) {
        float overallTime = RaceDao.instance.initiateARace(principal);
        if (overallTime > 0) {
            String challengee = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
            if (ChallengeDao.instance.respondToChallenge(id, challengee)) {
                if (ChallengeDao.instance.changeScores(challengee)) { // if we make this async it would be bazinga
                    return overallTime;
                }
                return -1;
            }
            return -1;
        }
        // TODO: decide if we are going to change the scores here
        return overallTime;
    }

    @PostMapping("/rest/rejectChallenge")
    public boolean rejectChallenge(@RequestParam int id, Principal principal) {
        String challengee = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        // TODO: decide if we are going to change the scores here
        return ChallengeDao.instance.deleteChallenge(challengee, id);
    }

    @GetMapping("/rest/getPendingChallengeRequests")
    public List<Challenge> getPendingChallengeRequests(Principal principal) {
        String challengee = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return ChallengeDao.instance.getPendingChallengeRequests(challengee);
    }

    @GetMapping("/rest/getSentChallengeRequests")
    public List<Challenge> getSendChallengeRequests(Principal principal) {
        String challenger = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return ChallengeDao.instance.getSentChallengeRequests(challenger);
    }
}
