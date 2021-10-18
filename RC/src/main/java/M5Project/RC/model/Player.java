package M5Project.RC.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Player {
    private String username;
    private String email;
    private String name;

    public Player() {
        ;
    }

    public Player(String username, String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = username;
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
