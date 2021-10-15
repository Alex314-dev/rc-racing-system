package M5Project.RC;

import M5Project.RC.model.Player;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RESTAPIController {
    //private static final String template = "Welcome %s!";


    @GetMapping("/player")
    public Player player(@RequestParam(value = "username", defaultValue = "USER") String username, @RequestParam(value = "email", defaultValue = "USER") String email)
    {
        return new Player(username, email);
    }
}
