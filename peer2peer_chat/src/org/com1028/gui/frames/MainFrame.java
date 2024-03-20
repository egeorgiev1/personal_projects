/** MainFrame.java */
package org.com1028.gui.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.com1028.database.LocalDB;
import org.com1028.gui.panel.conversations.ConversationsPanel;
import org.com1028.gui.panel.messages.MessagesPanel;
import org.com1028.model.ConversationListModel;
import org.com1028.model.OpenConversationModel;
import org.com1028.server.ChatServer;

/**
 * Contains the list of conversations, the currently open conversation, controls to send
 * messages and open other dialogs.
 *
 * @author Emil Georgiev
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = -4430761748569314630L;

    /** The conversations model */
    private ConversationListModel conversations    = new ConversationListModel();
    /** The currently open conversation model */
    private OpenConversationModel openConversation = new OpenConversationModel();
    /** The menu at the top of the frame */
    private MainFrameMenu         menu             = new MainFrameMenu(this);

    /**
     * Creates the frame and HTTPS server.
     */
    public MainFrame() {
        // Start chat server
        ChatServer.getInstance();

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                LocalDB.getInstance().closeConnection();
            }
        });

        ImageIcon bubbleIcon = new ImageIcon("images/bubble_icon.png", "bubble_icon");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Bubble");
        setIconImage(bubbleIcon.getImage());
        setMinimumSize(new Dimension(500, 250));
        setSize(800, 500);
        setLocationRelativeTo(null);

        setJMenuBar(menu);

        setLayout(new BorderLayout());
        add(new ConversationsPanel(conversations, openConversation), BorderLayout.WEST);

        add(new MessagesPanel(openConversation), BorderLayout.CENTER);
    }
}
