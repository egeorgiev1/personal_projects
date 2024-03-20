package tests;

import static org.junit.Assert.*;

import org.com1028.model.Conversation;
import org.junit.Test;

public class ConversationTests {

    @Test
    public void constructorTest1() {
        Conversation conv = new Conversation(1, "0.0.0.0", 123, "nickname", 13);
        assertEquals(conv.getId(), 1);
        assertEquals(conv.getIp(), "0.0.0.0");
        assertEquals(conv.getPort(), 123);
        assertEquals(conv.getNickname(), "nickname");
        assertEquals(conv.getMissedMessages(), 13);
    }

    @Test
    public void constructorTest2() {
        Conversation conv = new Conversation("0.0.0.0", 123, "nickname", 13);
        assertEquals(conv.getIp(), "0.0.0.0");
        assertEquals(conv.getPort(), 123);
        assertEquals(conv.getNickname(), "nickname");
        assertEquals(conv.getMissedMessages(), 13);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidIdTest() {
        new Conversation(-1, "0.0.0.0", 123, "nickname", 13);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullIpTest() {
        new Conversation(1, null, 123, "nickname", 13);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPortTest1() {
        new Conversation(1, "0.0.0.0", -1, "nickname", 13);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalPortTest2() {
        new Conversation(1, "0.0.0.0", 65536, "nickname", 13);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullNicknameTest() {
        new Conversation(1, "0.0.0.0", 123, null, 13);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeMissedMessagesException() {
        new Conversation(1, "0.0.0.0", 123, "nickname", -1);
    }

    @Test
    public void toStringTest() {
        Conversation conv = new Conversation(1, "0.0.0.0", 123, "nickname", 13);
        assertEquals(
                conv.toString(),
                "Conversation [id=" + 1 + ", ip=" + "0.0.0.0" + ", port=" + 123 + ", nickname=" + "nickname"
                + ", missedMessages=" + 13 + "]"
        );
    }
}
