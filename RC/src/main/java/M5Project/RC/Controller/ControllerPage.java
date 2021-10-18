package M5Project.RC.Controller;

import M5Project.RC.Dao.PlayerDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class ControllerPage {

    @GetMapping("/")
    public String loginPage() {

        return "login.html";
    }

    @GetMapping("/race")
    public String racePage(Principal principal) {

        System.out.println(PlayerDao.instance.getPlayer(principal.getName()).getEmail());
        System.out.println(PlayerDao.instance.getPlayer(principal.getName()).getName());
        System.out.println(PlayerDao.instance.getPlayer(principal.getName()).getUsername());

        return "race.html";
    }

    @GetMapping("/leaderboard")
    public String leaderboardPage() {
        return "leaderboard.html";
    }

    @GetMapping("/challanges")
    public String challangesPage() {
        return "challanges.html";
    }

    @GetMapping("/friends")
    public String friendPage() {
        return "friends.html";
    }

    @GetMapping("/newuser")
    public String newuserPage(HttpServletResponse response, Principal principal) throws IOException {

        if (!PlayerDao.instance.getPlayer(principal.getName()).getUsername().equals("")) {
            response.sendRedirect("/race");
        }
        return "newuser.html";
    }

}

