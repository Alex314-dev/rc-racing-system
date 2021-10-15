package M5Project.RC.Security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControllerPage {

    @GetMapping("/")
    public String helloWorld() {
        return "login.html";
    }

    @GetMapping("/restricted")
    public String restricted() {
        System.out.println("Logged");
        return "logged.html";
    }
}
