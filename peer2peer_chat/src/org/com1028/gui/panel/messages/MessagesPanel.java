/** MessagesPanel.java */
package org.com1028.gui.panel.messages;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.com1028.model.Message;
import org.com1028.model.OpenConversationModel;

/**
 * Shows the messages of the currently open conversation. Used to send, edit and delete messages.
 *
 * @author Emil Georgiev
 */
public class MessagesPanel extends JPanel {

    private static final long serialVersionUID = -7358114340092365936L;

    /**
     * Creates the panel
     *
     * @param model
     *          The model holding the currently open conversation and the messages of this conversation.
     */
    public MessagesPanel(OpenConversationModel model) {
        DefaultListModel<Message>        listModel           = new DefaultListModel<>();
        JVerticalScrollableList<Message> conversationList    = new JVerticalScrollableList<>(listModel);
        JScrollPane                      conversationsScroll = new JScrollPane(conversationList);;

        /* Make the messages list scroll to the bottom when the conversation is changed and
         * when a new message is received
         */
        ListDataListener scrollToBottom = new ListDataListener() {
            @Override
            public void intervalRemoved(ListDataEvent e) {
            }

            @Override
            public void intervalAdded(ListDataEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        conversationList.addComponentListener(new ComponentListener() {
                            @Override
                            public void componentHidden(ComponentEvent e) {
                            }

                            @Override
                            public void componentMoved(ComponentEvent e) {
                            }

                            @Override
                            public void componentResized(ComponentEvent e) {
                                int lastIndex = conversationList.getModel().getSize()-1;
                                if(lastIndex >= 0) {
                                    conversationList.ensureIndexIsVisible(lastIndex);
                                }
                                conversationList.removeComponentListener(this);
                            }

                            @Override
                            public void componentShown(ComponentEvent e) {
                            }
                        });
                    }
                });
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollToBottom.intervalAdded(null);
            }
        });

        conversationList.setCellRenderer(new MessageItemRenderer());

        /* Get the new model when a different conversation is opened and attach the scrollToBottom
         * listener to the new model
         */
        model.addObserver(new Observer() {
            @Override
            public void update(Observable arg0, Object arg) {
                if(arg == null) {
                    conversationList.setModel(model.getMessagesModel()); // bi triabvalo tova da e synchronized ot tozi, koito notifyva???
                    model.getMessagesModel().addListDataListener(scrollToBottom);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            scrollToBottom.intervalAdded(null);
                        }
                    });
                }
            }
        });

        // Invalidates the internal size cache so that the list item expands correctly with multiline text
        ComponentListener l = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                conversationList.setFixedCellHeight(10);
                conversationList.setFixedCellHeight(-1);
            }
        };
        conversationList.addComponentListener(l);

        // Open the edit and delete message context menu
        conversationList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ( SwingUtilities.isRightMouseButton(e) ) {
                    MessageActionsMenu actionsMenu = new MessageActionsMenu();
                    actionsMenu.show(
                            conversationList,
                            e.getX(),
                            e.getY()
                    );
                }
            }
        });

        setLayout(new BorderLayout());
        add(conversationsScroll, BorderLayout.CENTER);
        add(new SendMessagePanel(model), BorderLayout.SOUTH);
    }
}
