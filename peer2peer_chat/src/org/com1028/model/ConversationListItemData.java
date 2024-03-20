/** ConversationListItemData.java */
package org.com1028.model;

import java.util.Calendar;

/**
 * Holds the information for a single item of the conversation list. This includes the conversation object
 * and information about the most recent message of this conversation.
 *
 * @author Emil Georgiev
 */
public class ConversationListItemData {

    /** The conversation **/
    private Conversation conversation;
    /** The conversation's most recent message's content*/
    private String       recentMessageContent;
    /** The date the message was sent */
    private Calendar     date;

    /**
     * Creates an instance of the class
     *
     * @param conversation
     *          The conversation
     * @param recentMessageContent
     *          The content of the most recent message of the conversation
     * @param date
     *          The date the most recent message of the conversation was sent
     */
    public ConversationListItemData(Conversation conversation, String recentMessageContent, Calendar date) {
        setConversation(conversation);
        setRecentMessageContent(recentMessageContent);
        setDate(date);
    }

    /**
     * Get the conversation object
     *
     * @return
     *          The conversation object
     */
    public Conversation getConversation() {
        return this.conversation;
    }

    /**
     * Get the most recent message's content.
     *
     * @return
     *          The content of the message
     */
    public String getRecentMessageContent() {
        return this.recentMessageContent;
    }

    /**
     * Get the date of the most recent message of the conversation
     *
     * @return
     *          The date of the message
     */
    public Calendar getDate() {
        return (Calendar) this.date.clone();
    }

    /**
     * Set the conversation
     *
     * @param conversation
     *          The conversation to be set to the object
     */
    private void setConversation(Conversation conversation) {
        if(conversation == null) {
            throw new IllegalArgumentException("Error: conversation can't be null");
        }

        this.conversation = conversation;
    }

    /**
     * Set the content of the most recent message of the conversation
     *
     * @param content
     *          The message content
     */
    private void setRecentMessageContent(String content) {
        if(content == null) {
            throw new IllegalArgumentException("Error: content can't be null");
        }

        this.recentMessageContent = content;
    }

    /**
     * Set the date of the most recent message of the conversation
     *
     * @param date
     *          The date of the message
     */
    private void setDate(Calendar date) {
        if(date == null) {
            throw new IllegalArgumentException("Error: date can't be null");
        }

        this.date = (Calendar) date.clone();
    }
}
