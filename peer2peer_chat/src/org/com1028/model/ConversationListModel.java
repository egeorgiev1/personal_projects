/** ConversationListMode.java */
package org.com1028.model;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.com1028.database.LocalDB;
import org.com1028.server.CreateConversationHandler;
import org.com1028.server.HttpRequest;

/**
 * An observable model that's used to hold and manipulate the list of conversations
 *
 * @author Emil Georgiev
 */
public class ConversationListModel extends Observable {

    /** The list model that holds the conversations */
    private final DefaultListModel<ConversationListItemData> listModel = new DefaultListModel<>();

    /**
     * Creates the model
     */
    public ConversationListModel() {

        // Populate the conversation list with the database entries
        List<ConversationListItemData> convItems = LocalDB.getInstance().getRange();
        for(ConversationListItemData item: convItems) {
            listModel.addElement(item);
        }

        // Add new conversations to the model
        CreateConversationHandler.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable arg0, Object arg) {
                ConversationListItemData conv = (ConversationListItemData) arg;

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ConversationListModel.this.listModel.add(0, conv);
                    }
                });
            }
        });
    }

    /**
     * Creates a new conversation
     *
     * @param conversation
     *          The conversation to be created
     * @param initialMessage
     *          The initial message to be sent
     */
    public void createConversation(Conversation conversation, String initialMessage) {

        Map<String, String> params = new HashMap<String, String>();

        Calendar dateCalendar = Calendar.getInstance();
        String   date         = Long.toString(dateCalendar.getTimeInMillis());

        params.put("id", Integer.toString(conversation.getId()));
        params.put("port", Integer.toString(UserSettings.getInstance().getPort()));
        params.put("nickname", UserSettings.getInstance().getNickname());
        params.put("content", initialMessage);
        params.put("date", date);

        HttpRequest res = null;

        try {
            res = new HttpRequest(
                    conversation.getIp() + ":" + conversation.getPort(),
                    "create_conversation",
                    params
            );
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connection timeout.");
            return;
        } catch (ConnectException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connection refused.");
            return;
        }

        String remoteNickname = res.getResponseBody();

        if(res.getStatus() == 200) {
            // Try to add the conversation to the database. May get an id collision, although very unlikely
            Conversation convForDB = new Conversation(
                    conversation.getId(),
                    conversation.getIp(),
                    conversation.getPort(),
                    remoteNickname,
                    1
            );

            boolean success = LocalDB.getInstance().addConversation(convForDB);

            if(success) {
                // Add the initial message to the database
                LocalDB.getInstance().addMessage(
                    new Message(
                        convForDB.getId(),
                        initialMessage,
                        true,
                        dateCalendar
                    )
                );

                // Add the conversation to the model
                ConversationListItemData item = new ConversationListItemData(
                        convForDB,
                        initialMessage,
                        dateCalendar
                );

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        listModel.add(0, item);
                    }
                });
            }

        }
    }

    /**
     * Get the list model that holds the conversations
     *
     * @return
     *          The conversations list model
     */
    public DefaultListModel<ConversationListItemData> getConversationsModel() {
        return this.listModel;
    }
}
