package M5Project.RC.Security;

import M5Project.RC.Dao.PlayerDao;
import M5Project.RC.Resource.Database;
import M5Project.RC.model.Player;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Map;

@Controller
public class AfterLogin {

    @RequestMapping(value = "/logged", method = RequestMethod.GET)
    public ModelAndView afterLogin(Principal principal) {

        //get the user info from the response containing OauthCredentials
        String sub = principal.getName();
        String email = emailFromLoggedUser(principal);
        String name = nameFromLoggedUser(principal);

        Player newLoggedPlayer = new Player("", email, name);
        PlayerDao.instance.addPlayer(sub, newLoggedPlayer);

        //if the player is not present in the database its info needs to be inserted into the db
        if (!PlayerDao.instance.isPlayerRegistered(email)) {
            return new ModelAndView("redirect:" + "/newuser");
        }

        String username = PlayerDao.instance.getUsernameByEmail(email);
        newLoggedPlayer.setUsername(username);
        PlayerDao.instance.addPlayer(sub, newLoggedPlayer);

        return new ModelAndView("redirect:" + "/race");
    }

    private String emailFromLoggedUser(Principal principal) {
        OAuth2User oAuth2User = null;
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken)principal;

            oAuth2User = oAuth2AuthenticationToken.getPrincipal();
        }

        Map<String,Object> OauthAttributes =  oAuth2User.getAttributes();
        return (String) OauthAttributes.get("email");
    }

    public String nameFromLoggedUser(Principal principal) {
        OAuth2User oAuth2User = null;
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken)principal;

            oAuth2User = oAuth2AuthenticationToken.getPrincipal();
        }

        Map<String,Object> OauthAttributes =  oAuth2User.getAttributes();
        return (String) OauthAttributes.get("name");
    }


}
