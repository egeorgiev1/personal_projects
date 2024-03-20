/** Message.java */
package org.com1028.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

// make it immutable, stava mnogo po-lesno za managevane
/**
 * An immutable representation of a single message of a conversation
 *
 * @author Emil Georgiev
 */
public class Message {

    /** The database id of the message */
    private int      id;
    /** The database id of the conversation, to which the message belongs */
    private int      conversationId;
    /** The content of the message */
    private String   content;
    /** Tells if the message was made by the current user */
    private boolean  isMine;
    /** The date, on which the message was sent */
    private Calendar date;

    /**
     * Creates a message
     *
     * @param id
     *          The database id of the message
     * @param conversationId
     *          The database id of the conversation, to which the message belongs
     * @param content
     *          The content of the message
     * @param isMine
     *          Tells if the message was made by the current user
     * @param date
     *          The date, on whcih the message was sent
     */
    public Message(int id, int conversationId, String content, boolean isMine, Calendar date) {
        setId(id);
        setConversationId(conversationId);
        setContent(content);
        setIsMine(isMine);
        setDate(date);
    }

    /**
     * Creates a message without an id
     *
     * @param conversationId
     *          The database id of the conversation, to which the message belongs
     * @param content
     *          The content of the message
     * @param isMine
     *          Tells if the message was made by the current user
     * @param date
     *          The date, on whcih the message was sent
     */
    public Message(int conversationId, String content, boolean isMine, Calendar date) {
        setConversationId(conversationId);
        setContent(content);
        setIsMine(isMine);
        setDate(date);
    }

    /**
     * Get the database id of the message
     *
     * @return
     *          The message id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Get the database id of the conversation, to which the message belongs
     *
     * @return
     *          The conversation id
     */
    public int getConversationId() {
        return this.conversationId;
    }

    /**
     * Get the content of the message
     *
     * @return
     *          The content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Tells if the message was made by the current user
     *
     * @return
     *          Was the message made by the current user
     */
    public boolean isMine() {
        return this.isMine;
    }

    /**
     * Get a copy of the date, on which the message was sent
     *
     * @return
     *          Returns a copy of the sent date
     */
    public Calendar getDate() {
        return (Calendar) this.date.clone();
    }

    /**
     * Set the database id of the message.
     *
     * @param id
     *          The database id. Must be a non-negative integer.
     */
    private void setId(int id) {
        if(id < 0) {
            throw new IllegalArgumentException("Error: id can't be negative");
        }

        this.id = id;
    }

    /**
     * Set the database id of the conversation, to which the message belongs
     *
     * @param id
     *          The databse id. Must be a non-negative integer.
     */
    private void setConversationId(int id) {
        if(id < 0) {
            throw new IllegalArgumentException("Error: conversation_id can't be negative");
        }

        this.conversationId = id;
    }

    /**
     * Set the content of the message.
     *
     * @param content
     *          The content of the message.
     */
    private void setContent(String content) {
        if(content == null) {
            throw new IllegalArgumentException("Error: content can't be null");
        }

        this.content = content;
    }

    /**
     * Tell if the message was made by the current user
     *
     * @param isMine
     *          The given input
     */
    private void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    /**
     * Get a clone of the date, on which the message was sent
     *
     * @param date
     *          The clone of the sent date
     */
    private void setDate(Calendar date) {
        if(date == null) {
            throw new IllegalArgumentException("Error: date cant' be null");
        }

        this.date = (Calendar) date.clone();
    }

    /**
     * Get a string representation of the object
     *
     * @return
     *          The string representation
     */
    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "Message [id=" + id + ", conversation_id=" + conversationId + ", content=" + content + ", isMine="
                + isMine + ", date=" + formatter.format(this.date.getTime()) + "]";
    }
}
