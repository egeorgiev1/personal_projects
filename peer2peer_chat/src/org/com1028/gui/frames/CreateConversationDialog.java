/** CreateConversationDialog.java */
package org.com1028.gui.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.com1028.model.Conversation;
import org.com1028.model.ConversationListModel;
import org.com1028.model.UserSettings;

/**
 * A dialog used to create new conversations.
 *
 * @author Emil Georgiev
 */
public class CreateConversationDialog extends JDialog {

    private static final long serialVersionUID = -1018422780290986102L;

    /** Holds the content of the dialog */
    private JPanel contentPanel     = new JPanel();
    /** Holds the title of the dialog */
    private JLabel titleLabel       = new JLabel("Create Conversation");
    /** Combines the input panel and the action panel */
    private JPanel inputActionPanel = new JPanel();

    /** Used for collecting the user input */
    private JPanel     inputPanel = new JPanel();
    /** Annotates the ip field */
    private JLabel     ipLabel    = new JLabel("IP");
    /** Used to collect the ip */
    private JTextField ipField    = new JTextField();

    /** Annotates the port field */
    private JLabel     portLabel = new JLabel("Port");
    /** Used to collect the port */
    private JTextField portField = new JTextField();

    /** Annotates the initial message text area */
    private JLabel    contentLabel = new JLabel("Initial Message");
    /** Used to collect the initial message */
    private JTextArea contentArea  = new JTextArea();

    /** Holds the action buttons */
    private JPanel  dialogActionsPanel = new JPanel();
    /** Triggers the data validation and conversation creation methods */
    private JButton confirmButton      = new JButton("Create");
    /** Closes the dialog */
    private JButton cancelButton       = new JButton("Cancel");

    /**
     * Creates the dialog
     *
     * @param owner
     *          The window what created the dialog
     * @param model
     *          An instance of the model used to create the conversation
     */
    public CreateConversationDialog(JFrame owner, ConversationListModel model) {
        super(owner, "Bubble - Create Conversation", Dialog.ModalityType.APPLICATION_MODAL);

        // Configure the frame
        ImageIcon bubbleIcon = new ImageIcon("images/bubble_icon.png", "bubble_icon");

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setIconImage(bubbleIcon.getImage());
        setBounds(100, 100, 270, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        // Configure the input panel
        ipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ipField.setAlignmentX(Component.LEFT_ALIGNMENT);
        portLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        portField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(new EmptyBorder(10, 20, 0, 20));
        inputPanel.add(ipLabel);
        inputPanel.add(ipField);
        inputPanel.add(portLabel);
        inputPanel.add(portField);
        inputPanel.add(contentLabel);

        // Configure the dialog actions panel
        dialogActionsPanel.add(confirmButton);
        dialogActionsPanel.add(cancelButton);

        JScrollPane scrollableTextArea = new JScrollPane(contentArea);
        scrollableTextArea.setBorder(
            new CompoundBorder(
                    new EmptyBorder(0, 20, 0, 20),
                    BorderFactory.createLineBorder(Color.GRAY)
            )
        );

        // Combine the input and actions panels
        inputActionPanel.setLayout(new BorderLayout(0, 0));
        inputActionPanel.add(inputPanel, BorderLayout.NORTH);
        inputActionPanel.add(scrollableTextArea, BorderLayout.CENTER);
        inputActionPanel.add(dialogActionsPanel, BorderLayout.SOUTH);

        // Create a label above the input fields holding the dialog name
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog.Bold", Font.PLAIN, 15));

        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(inputActionPanel, BorderLayout.CENTER);

        confirmButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                disableInput();

                String id      = Integer.toString((int)(Math.random()*Integer.MAX_VALUE+1));
                String ip      = ipField.getText();
                String port    = portField.getText();
                String content = contentArea.getText();

                if(!validateFields()) {
                    return;
                }

                model.createConversation(
                        new Conversation(
                                Integer.parseInt(id),
                                ip,
                                Integer.parseInt(port),
                                UserSettings.getInstance().getNickname(),
                                1
                        ),
                        content
                );

                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setContentPane(contentPanel);
    }

    /**
     * Validates the dialog fields
     *
     * @return
     *          Tells if the data is valid or not
     */
    private boolean validateFields() {
        String ip      = ipField.getText();
        String port    = portField.getText();
        String content = contentArea.getText();

        if(ip.length() == 0) {
            JOptionPane.showMessageDialog(null, "No ip specified");
            enableInput();
            return false;
        }

        String ipRegex = ""
                + "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}"
                + "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";

        if(!ip.matches(ipRegex)) {
            JOptionPane.showMessageDialog(null, "IP must be a valid IPv4 string");
            enableInput();
            return false;
        }

        if(port.length() == 0) {
            JOptionPane.showMessageDialog(null, "No port specified");
            enableInput();
            return false;
        }

        int portInteger = 0;
        try {
             portInteger = Integer.parseInt(port);
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Port is not a valid integer");
            enableInput();
            return false;
        }

        if(portInteger < 0 || portInteger > 65535) {
            JOptionPane.showMessageDialog(null, "Port can only be between 0 and 65535 inclusive");
            enableInput();
            return false;
        }

        if(content.length() == 0) {
            JOptionPane.showMessageDialog(null, "No initial message specified");
            enableInput();
            return false;
        }

        CreateConversationDialog.this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        confirmButton.setEnabled(true);
        cancelButton.setEnabled(true);

        return true;
    }

    /**
     * Enables the input of the dialog.
     */
    private void enableInput() {
        CreateConversationDialog.this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        confirmButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * Disables the input of the dialog.
     */
    private void disableInput() {
        CreateConversationDialog.this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        confirmButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }
}
