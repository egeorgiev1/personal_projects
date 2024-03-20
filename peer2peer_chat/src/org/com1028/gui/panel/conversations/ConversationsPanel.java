/** ConversationsPanel.java */
package org.com1028.gui.panel.conversations;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.com1028.gui.frames.CreateConversationDialog;
import org.com1028.model.ConversationListItemData;
import org.com1028.model.ConversationListModel;
import org.com1028.model.Message;
import org.com1028.model.OpenConversationModel;
import org.com1028.server.ReceiveMessageHandler;

/**
 * Shows all conversations in the database. Used to change the currently open conversation and listens
 * to other objects that want to add new conversations.
 *
 * @author Emil Georgiev
 */
public class ConversationsPanel extends JPanel {

    private static final long serialVersionUID = -4904329054989323965L;

    /**
     * Creates the panel.
     *
     * @param convModel
     *          The model that holds all conversations.
     * @param msgModel
     *          The model that holds the currently open conversation.
     */
    public ConversationsPanel(ConversationListModel convModel, OpenConversationModel msgModel) {
        DefaultListModel<ConversationListItemData> listModel        = convModel.getConversationsModel();
        JList<ConversationListItemData>            conversationList = new JList<>(listModel);

        conversationList.setCellRenderer(new ConversationItemRenderer());
        conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Open the selected conversation
        conversationList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ConversationListItemData item = conversationList.getSelectedValue();
                if(item != null) {
                    msgModel.openConversation(item.getConversation());
                }
            }
        });

        // Listen to the model if a new conversation needs to be added
        convModel.addObserver(new Observer() {
            @Override
            public void update(Observable arg0, Object arg) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if(arg != null) {
                            listModel.addElement((ConversationListItemData) arg);
                        }
                    }
                });
            }
        });

        // Puts the conversation with the most recent message to the top
        Observer updateConversationList = new Observer() {
            @Override
            public void update(Observable arg0, Object arg) {
                if(arg != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        Message msg = (Message)arg;
                        @Override
                        public void run() {
                            Enumeration<ConversationListItemData> conversations = listModel.elements();
                            while(conversations.hasMoreElements()) {
                                ConversationListItemData item = conversations.nextElement();
                                if(item.getConversation().getId() == msg.getConversationId()) {
                                    listModel.removeElement(item);
                                    ConversationListItemData newItem = new ConversationListItemData(
                                            item.getConversation(),
                                            msg.getContent(),
                                            msg.getDate()
                                    );
                                    listModel.add(0, newItem);
                                }
                            }
                        }
                    });
                }
            }
        };

        msgModel.addObserver(updateConversationList);
        ReceiveMessageHandler.getInstance().addObserver(updateConversationList);

        // Open the create conversation dialog
        StartConversationButton startConversationButton = new StartConversationButton();
        startConversationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateConversationDialog dialog = new CreateConversationDialog(
                        (JFrame)SwingUtilities.getWindowAncestor(ConversationsPanel.this),
                        convModel
                );
                dialog.setVisible(true);
            }
        });

        setLayout(new BorderLayout());
        add(new JScrollPane(conversationList), BorderLayout.CENTER);
        add(startConversationButton, BorderLayout.NORTH);
    }
}
