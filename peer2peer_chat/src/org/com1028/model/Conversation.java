/** Conversation.java */
package org.com1028.model;

/**
 * An immutable representation of a conversation
 *
 * @author Emil Georgiev
 */
public class Conversation {

    /** The database id of the conversation */
    private int    id;
    /** The IP of the conversation partner in IPv4 format */
    private String ip;
    /** The port of the conversation partner */
    private int    port;
    /** The nickname of the conversation partner */
    private String nickname;
    /** The count of missed messages by the user for the conversation */
    private int    missedMessages;

    /**
     * Creates the conversation
     *
     * @param id
     *          The database id of the conversation
     * @param ip
     *          The IP of the conversation partner in IPv4 format
     * @param port
     *          The port of the conversation partner
     * @param nickname
     *          The nickname of the conversation partner
     * @param missedMessages
     *          The count of missed messages by the user for the conversation
     */
    public Conversation(int id, String ip, int port, String nickname, int missedMessages) {
        setId(id);
        setIp(ip);
        setPort(port);
        setNickname(nickname);
        setMissedMessages(missedMessages);
    }

    /**
     * Creates a conversation without an id
     *
     * @param ip
     *          The IP of the conversation partner in IPv4 format
     * @param port
     *          The port of the conversation partner
     * @param nickname
     *          The nickname of the conversation partner
     * @param missedMessages
     *          The count of missed messages by the user for the conversation
     */
    public Conversation(String ip, int port, String nickname, int missedMessages) {
        setIp(ip);
        setPort(port);
        setNickname(nickname);
        setMissedMessages(missedMessages);
    }

    /**
     * Get the database id of the conversation
     *
     * @return
     *          The databse id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get the IP of the conversation partner
     *
     * @return
     *          The IP
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * Get the port of the conversation partner
     *
     * @return
     *          The port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Get the nickname of the conversation partner
     *
     * @return
     *          The nickname
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Get the missed messages count by the user
     *
     * @return
     *          The missed messages count
     */
    public int getMissedMessages() {
        return this.missedMessages;
    }

    /**
     * Set the database id of the conversation.
     *
     * @param id
     *          The database id. Must be a non-negative positive integer
     */
    private void setId(int id) {
        if(id < 0) {
            throw new IllegalArgumentException("Error: id can't be negatve");
        }

        this.id = id;
    }

    /**
     * Set the IP of the conversation partner
     *
     * @param ip
     *          The IP to be set. Must be in IPv4 format
     */
    private void setIp(String ip) {
        if(ip == null) {
            throw new IllegalArgumentException("Error: ip can't be null");
        }

        this.ip = ip;
    }

    /**
     * Sets the port of the conversation partner
     *
     * @param port
     *          The port to be set. Must be between 0 and 65535 inclusive
     */
    private void setPort(int port) {
        if(port < 0 || port > 65535) {
            throw new IllegalArgumentException("Error: invalid port");
        }

        this.port = port;
    }

    /**
     * Set the nickname of the conversation partner
     *
     * @param nickname
     *          The nickname to be set.
     */
    private void setNickname(String nickname) {
        if(nickname == null) {
            throw new IllegalArgumentException("Error: nickname can't be null");
        }

        this.nickname = nickname;
    }

    /**
     * Set the missed messages count
     *
     * @param missedMessages
     *          The count to be set
     */
    private void setMissedMessages(int missedMessages) {
        if(missedMessages < 0) {
            throw new IllegalArgumentException("Error: missedMessages can't be negative");
        }

        this.missedMessages = missedMessages;
    }

    /**
     * Returns a string representation of the object
     *
     * @return
     *          The generated string
     */
    @Override
    public String toString() {
        return "Conversation [id=" + id + ", ip=" + ip + ", port=" + port + ", nickname=" + nickname
                + ", missedMessages=" + missedMessages + "]";
    }
}
