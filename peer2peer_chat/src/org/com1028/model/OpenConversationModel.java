/** OpenConversationModel.java */
package org.com1028.model;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.com1028.database.LocalDB;
import org.com1028.server.HttpRequest;
import org.com1028.server.ReceiveMessageHandler;

/**
 * The model that holds the currently open conversation and all it's messages. Used to send messages
 * to the conversation partner.
 *
 * @author Emil Georgiev
 */
public class OpenConversationModel extends Observable {

    /** The currently open conversation */
    private Conversation              conversation;
    /** The list model that holds the messages of the currently open conversation */
    private DefaultListModel<Message> listModel;
    /** A lock used to eliminate interleaving with the changing of the currently open conversation */
    private final Lock                conversationChanging;

    /**
     * Creates the model
     */
    public OpenConversationModel() {
        conversationChanging = new ReentrantLock();

        // Add messages from the server to the model if they're for the currently open conversation
        ReceiveMessageHandler handler = ReceiveMessageHandler.getInstance();
        handler.addObserver(new Observer() {
            @Override
            public void update(Observable arg0, Object arg) {
                Message msg = (Message) arg;

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        conversationChanging.lock();

                        if(msg.getConversationId() == OpenConversationModel.this.conversation.getId()) {
                            OpenConversationModel.this.listModel.addElement(msg);
                        }

                        conversationChanging.unlock();
                    }
                });
            }
        });
    }

    /**
     * Opens the given conversation
     *
     * @param conversation
     *          The conversation to be opened
     */
    public void openConversation(Conversation conversation) {
        conversationChanging.lock();

        this.conversation = conversation;
        this.listModel    = new DefaultListModel<>();

        List<Message> messages = LocalDB.getInstance().getRange(conversation.getId());
        for(Message msg: messages) {
            listModel.add(0, msg);
        }

        setChanged();
        notifyObservers();

        conversationChanging.unlock();
    }

    /**
     * Sends a new message to the conversation partner of the currently open conversation
     *
     * @param content
     *          The content of the message to be sent
     */
    public void sendMessage(String content) {
        if(this.conversation == null || content.length() == 0) {
            return;
        }

        // Add message to the db
        Message messageForDB = new Message(
                OpenConversationModel.this.conversation.getId(),
                content,
                true,
                Calendar.getInstance()
        );

        int id = LocalDB.getInstance().addMessage(messageForDB);

        // Add message to the model
        Message messageForModel = new Message(
                id,
                OpenConversationModel.this.conversation.getId(),
                content,
                true,
                messageForDB.getDate()
        );

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                conversationChanging.lock();

                if(messageForModel.getConversationId() == OpenConversationModel.this.conversation.getId()) {
                    OpenConversationModel.this.listModel.addElement(messageForModel);
                }

                conversationChanging.unlock();
            }
        });

        // Send message
        Thread sendMessageThread = new Thread() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("conversationid", Integer.toString(conversation.getId()));
                params.put("content", content);
                params.put("date", Long.toString(Calendar.getInstance().getTimeInMillis()));

                HttpRequest res = null;
                try {
                    res = new HttpRequest(
                            conversation.getIp() + ":" + conversation.getPort(),
                            "send_message",
                            params
                    );
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Connection timeout.");
                } catch (ConnectException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Connection refused.");
                }

                /* If unsuccessful, remove message from db and model if the conversation for
                 * this message is still open
                 */
                if(res == null || res.getStatus() != 200) {
                    LocalDB.getInstance().removeMessage(messageForModel);

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            conversationChanging.lock();

                            if(messageForModel.getConversationId() == OpenConversationModel.this.conversation.getId()) {
                                OpenConversationModel.this.listModel.removeElement(messageForModel);
                            }

                            conversationChanging.unlock();
                        }
                    });
                }
            }
        };

        sendMessageThread.start();

        setChanged();
        notifyObservers(messageForModel);
    }

    /**
     * Returns the list model that holds the messages of the currently open conversation
     *
     * @return
     *          The list model
     */
    public DefaultListModel<Message> getMessagesModel() {
        if(this.listModel == null) {
            return new DefaultListModel<Message>();
        }

        return this.listModel;
    }
}