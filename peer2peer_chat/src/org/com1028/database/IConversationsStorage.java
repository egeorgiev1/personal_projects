/** IConversationsStorage.java */
package org.com1028.database;

import java.util.List;

import org.com1028.model.Conversation;
import org.com1028.model.ConversationListItemData;

/**
 * Encapsulates the database interface for the conversations.
 *
 * @author Emil Georgiev
 */
public interface IConversationsStorage {
    /**
     * Adds a new conversation to the database.
     *
     * @param conversation
     *          The object representing the conversation.
     * @return
     *          Tells if the operation was successful.
     */
    public boolean addConversation(Conversation conversation);

    /**
     * Changes data for the given conversation in the database.
     *
     * @param conversation
     *          The object holding the changed data and the id of the conversation.
     */
    public void editConversation(Conversation conversation);

    /**
     * Removes the given conversation from the database.
     *
     * @param conversation
     *          The object representing the conversation.
     */
    public void removeConversation(Conversation conversation);

    /**
     * Creates a list of objects representing all conversations in the database and information
     * about their most recent message.
     *
     * @return
     *          The list of conversations and data about their most recent message.
     */
    public List<ConversationListItemData> getRange();
}
