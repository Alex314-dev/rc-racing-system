package M5Project.RC.Test;
import M5Project.RC.Resource.DBFriendship;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import M5Project.RC.model.Challenge;
import M5Project.RC.model.Race;
import M5Project.RC.model.Player;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDBFriends {

    static final String USER = "LoopingLaurens";

    /**
     * Test that we get all friends with their corresponding wins and losses for a given user
     */
    @Test
    @Order(1)
    void checkGetAllFriendsWinsLosses() {
        List<Player> friends = DBFriendship.getFriendsWinsLosses(USER);
        assertEquals(4, friends.size());
    }

    /**
     * Test that we get all pending friend requests for a given user
     */
    @Test
    @Order(2)
    void checkGetPendingRequests() {
        List<String> pending = DBFriendship.getRequests(USER, true);
        assertEquals(2, pending.size());
    }

    /**
     * Test that we get all sent friend requests for a given user
     */
    @Test
    @Order(3)
    void checkGetSentRequests() {
        List<String> sent = DBFriendship.getRequests("KrisCross", false);
        assertEquals(1, sent.size());
    }

    /**
     * Test that a correct friends request is sent
     */
    @Test
    @Order(4)
    void checkAddAFriend() {
        int flag = DBFriendship.sendFriendRequest("SexyBeast", "KrisCross");
        assertEquals(0, flag);
    }

    /**
     * Test that a friend request is not sent when this request is already sent
     */
    @Test
    @Order(5)
    void checkAddFriendAlreadyInRequest() {
        int flag = DBFriendship.sendFriendRequest("SexyBeast", "KrisCross");
        assertEquals(2, flag);
    }

    /**
     * Test that a friend request is not sent when the 2 users are already friends
     */
    @Test
    @Order(6)
    void checkAddFriendAlreadyFriends() {
        int flag = DBFriendship.sendFriendRequest("SexyBeast", USER);
        assertEquals(1, flag);
    }

    /**
     * Test that a user can respond to a friend request
     */
    @Test
    @Order(7)
    void checkRespondingToRequest() {
        List<Player> friends = DBFriendship.getFriendsWinsLosses("KrisCross");
        boolean hasFriend = false;

        for (Player friend: friends) {
            if (friend.getUsername().equals("SexyBeast")) {
                hasFriend = true;
            }
        }
        assertFalse(hasFriend); //SexyBeast is still not a friend of KrisCross

        DBFriendship.respondToRequest("SexyBeast", "KrisCross");

        List<Player> friends1 = DBFriendship.getFriendsWinsLosses("KrisCross");
        for (Player friend: friends1) {
            if (friend.getUsername().equals("SexyBeast")) {
                hasFriend = true;
            }
        }

        assertTrue(hasFriend);
    }

    /**
     * Test the proper deletion of a friendship
     */
    @Test
    @Order(8)
    void checkDeleteFriendship() {
        List<Player> friends = DBFriendship.getFriendsWinsLosses("KrisCross");
        boolean hasFriend1 = false;

        for (Player friend: friends) {
            if (friend.getUsername().equals("SexyBeast")) {
                hasFriend1 = true;
            }
        }

        assertTrue(hasFriend1);

        DBFriendship.deleteFriend("SexyBeast", "KrisCross");

        List<Player> friends1 = DBFriendship.getFriendsWinsLosses("KrisCross");

        boolean hasFriend2 = false;

        for (Player friend: friends1) {
            if (friend.getUsername().equals("SexyBeast")) {
                hasFriend2 = true;
            }
        }

        assertFalse(hasFriend2);
    }
}
