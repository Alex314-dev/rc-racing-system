package M5Project.RC.Controller;

import M5Project.RC.Dao.ChallengeDao;
import M5Project.RC.Dao.FriendDao;
import M5Project.RC.Dao.PlayerDao;
import M5Project.RC.Dao.RaceDao;
import M5Project.RC.JavaClientSocket.ClientSocket;
import M5Project.RC.model.Challenge;
import M5Project.RC.model.ErrorMessage;
import M5Project.RC.model.Player;
import M5Project.RC.model.Race;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.PatternSyntaxException;

@RestController
@CrossOrigin
public class RESTAPIController {

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
    @GetMapping("/rest/logout")
    public void logout(HttpServletResponse response, Principal principal) throws IOException {
        PlayerDao.instance.removePlayerFromMap(principal.getName());

        response.sendRedirect("/logout");
    }

    @GetMapping("/rest/allraces")
    public List<Race> allRaces() {
        return RaceDao.instance.getRaces(null);
    }

    @GetMapping("/rest/myraces")
    public List<Race> myRaces(Principal principal) {
        return RaceDao.instance.getRaces(PlayerDao.instance.getPlayer(principal.getName()).getUsername());
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
            return false; // you are not the one racing
        }
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
                if (ChallengeDao.instance.startNewChallenge(challenger, challengee)) {
                    return overallTime;
                }
                return ErrorMessage.SERVER_ERROR;
            }
            // in this scenario we have the challenger sending a request but getting an invalid race
            // this will update the scores but there will not be an entry in the challenge table about this.
            if (overallTime != ErrorMessage.ONGOING_RACE) {
                ChallengeDao.instance.changeScoresInvalidRace(-1, challenger, challengee, false);
            }
            return overallTime;
        }
        return ErrorMessage.ONGOING_CHALLENGE;
    }

    @PostMapping("/rest/acceptChallenge")
    public float acceptChallenge(@RequestParam String challenger, @RequestParam int id, Principal principal) {
        String challengee = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        float overallTime = RaceDao.instance.initiateARace(principal);
        if (overallTime > 0) {
            if (ChallengeDao.instance.respondToChallenge(id, challengee) && ChallengeDao.instance.changeScores(challengee)) {
                return overallTime;
            }
            return ErrorMessage.SERVER_ERROR;
        }

        if (overallTime != ErrorMessage.ONGOING_RACE && ChallengeDao.instance.checkIfChallengeExists(challenger, challengee, id)) {
            ChallengeDao.instance.changeScoresInvalidRace(id, challenger, challengee, true);
        }
        return overallTime;
    }

    @PostMapping("/rest/rejectChallenge")
    public boolean rejectChallenge(@RequestParam String challenger, @RequestParam int id, Principal principal) {
        String challengee = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        if (ChallengeDao.instance.checkIfChallengeExists(challenger, challengee, id)) {
            return ChallengeDao.instance.changeScoresInvalidRace(id, challenger, challengee, true);
        }
        return false;
    }

    @GetMapping("/rest/getDoneChallenges")
    public List<Challenge> getAllDoneChallenges(Principal principal) {
        String username = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return ChallengeDao.instance.getDoneChallenges(username);
    }

    @GetMapping("/rest/getPendingChallengeRequests")
    public List<Challenge> getPendingChallengeRequests(Principal principal) {
        String challengee = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return ChallengeDao.instance.getPendingChallengeRequests(challengee);
    }

    @GetMapping("/rest/getSentChallengeRequests")
    public List<Challenge> getSentChallengeRequests(Principal principal) {
        String challenger = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return ChallengeDao.instance.getSentChallengeRequests(challenger);
    }

    @PostMapping("/rest/sendFriendRequest")
    public int sendFriendRequest(@RequestParam String friendToAdd, Principal principal) {
        System.out.println("FRIEND = " + friendToAdd);
        String sender = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return FriendDao.instance.sendFriendRequest(sender, friendToAdd);
    }

    @PostMapping("/rest/acceptFriendRequest")
    public int acceptFriendRequest(@RequestParam String friendToAccept, Principal principal) {
        String current = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return FriendDao.instance.acceptFriendRequest(current, friendToAccept);
    }

    @GetMapping("/rest/getPendingRequests")
    public List<String> getPendingRequests(Principal principal) {
        String username = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return FriendDao.instance.getPendingRequests(username);
    }

    @GetMapping("/rest/getSentRequests")
    public List<String> getSentRequests(Principal principal) {
        String username = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return FriendDao.instance.getSentRequests(username);
    }

    @GetMapping("/rest/getFriendsWinsLosses")
    public List<Player> getFriendsWinsLosses(Principal principal) {
        String username = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return FriendDao.instance.getFriendsWinsLosses(username);
    }

    @DeleteMapping("/rest/deleteFriend")
    public int deleteFriend(@RequestParam String friendToDelete, Principal principal) {
        String current = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return FriendDao.instance.deleteFriend(current, friendToDelete);
    }

    @DeleteMapping("/rest/removeAccount")
    public int removeAccount(Principal principal) {
        String player = PlayerDao.instance.getPlayer(principal.getName()).getUsername();
        return PlayerDao.instance.removeAccount(player);
    }
}
