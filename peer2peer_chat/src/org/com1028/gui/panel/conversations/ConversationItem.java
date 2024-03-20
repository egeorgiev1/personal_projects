/** ConversationItem.java */
package org.com1028.gui.panel.conversations;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Represents a single conversation in a JList
 *
 * @author Emil Georgiev
 */
public class ConversationItem extends JPanel {

    private static final long serialVersionUID = 1296508614329042300L;

    /** Shows the nickname of the conversation partner and the last message in the conversation */
    private JPanel columnPanel    = new JPanel();
    /** Shows the nickname of the conversation partner */
    private JLabel partnerName    = new JLabel();
    /** Shows the last message in the conversation */
    private JLabel lastMessage    = new JLabel();
    /** Shows the number of missed messages by the user */
    private JLabel missedMessages = new JLabel();

    /**
     * Creates the conversation list item
     */
    public ConversationItem() {
        partnerName.setFont(new Font("Dialog.Bold", Font.PLAIN, 13));
        lastMessage.setFont(new Font("Dialog", Font.PLAIN, 11));
        lastMessage.setForeground(Color.GRAY);

        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.Y_AXIS));
        columnPanel.setPreferredSize(new Dimension(150, 35));
        columnPanel.setOpaque(false);
        columnPanel.add(partnerName);
        columnPanel.add(lastMessage);

        missedMessages.setBorder(new EmptyBorder(10,10,10,10));
        missedMessages.setOpaque(false);

        add(columnPanel);
        add(missedMessages);
    }

    /**
     * Sets the nickname of the conversation partner
     *
     * @param name
     *          The given nickname.
     */
    public void setPartnerName(String name) {
        partnerName.setText(name);
    }

    /**
     * Sets the last message label
     *
     * @param message
     *          The last message
     */
    public void setLastMessage(String message) {
        lastMessage.setText(message);
    }

    /**
     * Sets the missed messages count
     *
     * @param count
     *          The give missed messages count
     */
    public void setMissedMessagesCount(int count) {
        missedMessages.setText(Integer.toString(count));
    }
}
