/** SendMessagePanel.java */
package org.com1028.gui.panel.messages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.com1028.model.OpenConversationModel;

/**
 * Used to collect a new message from the user to be sent
 *
 * @author Emil Georgiev
 */
public class SendMessagePanel extends JPanel {

    private static final long serialVersionUID = 3711221597440466515L;

    /** Sends the message */
    private JButton sendButton = new JButton();
    /** Collects the message from the user */
    private JTextArea textArea = new JTextArea();

    /**
     * Creates the panel
     *
     * @param model
     *          The model holding the currently open conversation
     */
    public SendMessagePanel(OpenConversationModel model) {

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.sendMessage(textArea.getText());
            }
        });

        ImageIcon sendIcon = new ImageIcon("images/send_icon.png", "plus_icon");
        sendIcon = new ImageIcon(sendIcon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH));

        sendButton.setIcon(sendIcon);
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setMinimumSize(new Dimension(40, 40));
        sendButton.setBackground(Color.decode("0xDDDDDD"));

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMaximumSize(new Dimension(100, 100));

        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        setLayout(new BorderLayout());
        add(textArea, BorderLayout.CENTER);
        add(sendButton, BorderLayout.EAST);
    }
}
