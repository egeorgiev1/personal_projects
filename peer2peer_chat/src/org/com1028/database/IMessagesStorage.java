/** IMessagesStorage.java */
package org.com1028.database;

import java.util.List;

import org.com1028.model.Message;

/**
 * Encapsulates the database interface for the messages.
 *
 * @author Emil Georgiev
 */
public interface IMessagesStorage {
    /**
     * Adds a message to the database.
     *
     * @param message
     *          The message object to add.
     * @return
     *          The id of the added message.
     */
    public int addMessage(Message message);

    /**
     * Edits the message content in the database, does nothing if no message found.
     *
     * @param message
     *          The object used to edit the message in the database.
     */
    public void editMessage(Message message);

    /**
     * Removes a message from the database.
     *
     * @param message
     *          The object used to find the message in the database.
     */
    public void removeMessage(Message message);

    /**
     * Returns a list of messages for the specified conversation.
     *
     * @param conversationId
     *          The id of the conversation the messages belong to.
     * @return
     *          The list of messages that belong to the conversation.
     */
    public List<Message> getRange(int conversationId);
}
