package M5Project.RC.Controller;

import M5Project.RC.Dao.PlayerDao;
import M5Project.RC.Security.AfterLogin;
import M5Project.RC.model.Player;
import M5Project.RC.Resource.Database;
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

}
