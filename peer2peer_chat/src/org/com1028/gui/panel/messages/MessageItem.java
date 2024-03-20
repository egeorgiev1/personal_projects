/** MessageItem.java */
package org.com1028.gui.panel.messages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Represents a single message bubble
 *
 * @author Emil Georgiev
 */
public class MessageItem extends JPanel {

    private static final long serialVersionUID = 2L;

    /** Shows the data, on which the message was sent */
    private JLabel    sendDate = new JLabel();
    /** Shows the message itself */
    private JTextArea message  = new JTextArea();

    /** Represents the message bubble */
    private JPanel bubble = new JPanel();
    /** Space used to distinguish between the conversation partners message bubbles */
    private JPanel space  = new JPanel();

    /** Grid constraints used for the bubble */
    private GridBagConstraints bubbleConstraints = new GridBagConstraints();
    /** Grid constraints used for the space */
    private GridBagConstraints spaceConstraints  = new GridBagConstraints();

    /**
     * Creates the message bubble
     */
    public MessageItem() {

        sendDate.setFont(new Font("Dialog", Font.PLAIN, 12));
        sendDate.setForeground(Color.DARK_GRAY);
        sendDate.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Turn the textarea into a multiline label
        message.setOpaque(false);
        message.setEditable(false);
        message.setFocusable(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setBorder(new EmptyBorder(0, 0, 0, 0));
        message.setFont(new Font("Dialog.Bold", Font.PLAIN, 12));
        message.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create the message bubble
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(new EmptyBorder(5,5,5,5));
        bubble.setBackground(Color.lightGray);
        bubble.add(sendDate, BorderLayout.NORTH);
        bubble.add(message, BorderLayout.CENTER);

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(5,10,5,10));

        spaceConstraints.fill = GridBagConstraints.HORIZONTAL;
        spaceConstraints.weightx = 0;
        spaceConstraints.gridx = 0;
        spaceConstraints.gridy = 0;
        space.setPreferredSize(new Dimension(50, 5));
        space.setMinimumSize(new Dimension(50, 5));

        bubbleConstraints.fill = GridBagConstraints.HORIZONTAL;
        bubbleConstraints.weightx = 3;
        bubbleConstraints.gridx = 1;
        bubbleConstraints.gridy = 0;

        add(space, spaceConstraints);
        add(bubble, bubbleConstraints);
    }

    /**
     * Sets the date the messages was sent
     *
     * @param date
     *          The date the message was sent
     */
    public void setSendDate(Calendar date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sendDate.setText(formatter.format(date.getTime()));
    }

    /**
     * Sets the message content
     *
     * @param message
     *          The message to be set to the bubble
     */
    public void setMessage(String message) {
        this.message.setText(message);
    }

    /**
     * Set the direction of the bubble
     *
     * @param isMine
     *          Tells to which partner the message belongs
     */
    public void setIsMine(boolean isMine) {
        remove(bubble);
        remove(space);

        if(isMine) {
            bubble.setBackground(Color.LIGHT_GRAY);
            bubbleConstraints.gridx = 1;
            spaceConstraints.gridx = 0;
        } else {
            bubble.setBackground(Color.decode("0xDDDDDD"));
            bubbleConstraints.gridx = 0;
            spaceConstraints.gridx = 1;
        }

        add(bubble, bubbleConstraints);
        add(space, spaceConstraints);
        revalidate();
        repaint();
    }
}
