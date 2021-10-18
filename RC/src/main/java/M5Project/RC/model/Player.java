package M5Project.RC.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Player {
    private String username;
    private String email;

    public Player() {
        ;
    }

    public Player(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Id
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.email, this.username);
    }

    @Override
    public String toString() {
        return "Player{" + "email=" + this.email + ", username='" + this.username + '\'' + '}';
    }

}
