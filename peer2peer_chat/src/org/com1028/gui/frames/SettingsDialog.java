/** SettingsDialog.java */
package org.com1028.gui.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.com1028.model.UserSettings;

/**
 * Creates the dialog used to change the application settings.
 *
 * @author Emil Georgiev
 */
public class SettingsDialog extends JDialog {

    private static final long serialVersionUID = -1018422780290986102L;

    /** Holds all the dialog content */
    private JPanel contentPanel     = new JPanel();
    /** Shows the title of the dialog at the top */
    private JLabel titleLabel       = new JLabel("User Settings");
    /** Combines the input panel and the action panel */
    private JPanel inputActionPanel = new JPanel();

    /** Holds the GUI that collects and displays the application settings */
    private JPanel     inputPanel    = new JPanel();
    /** Annotates the nickname field */
    private JLabel     nicknameLabel = new JLabel("Nickname");
    /** Used to show the current nickname and collect the new one */
    private JTextField nicknameField = new JTextField();

    /** Annotates the port field */
    private JLabel     portLabel = new JLabel("Port");
    /** USed to show the current port and collect the new one */
    private JTextField portField = new JTextField();

    /** Holds buttons to perform actions */
    private   JPanel  dialogActionsPanel = new JPanel();
    /** Validates the data in the fields and saves the settings */
    protected JButton confirmButton      = new JButton("Confirm");
    /** Closes the dialog */
    private   JButton cancelButton       = new JButton("Cancel");

    /**
     * Creates the dialog
     *
     * @param owner
     *          The window that created the dialog.
     */
    public SettingsDialog(JFrame owner) {
        super(owner, "Bubble - User Settings", Dialog.ModalityType.APPLICATION_MODAL);

        nicknameField.setText(UserSettings.getInstance().getNickname());
        portField.setText(Integer.toString(UserSettings.getInstance().getPort()));

        // Move caret to the end
        nicknameField.setCaretPosition(nicknameField.getDocument().getLength());

        // Configure the frame
        ImageIcon bubbleIcon = new ImageIcon("images/bubble_icon.png", "bubble_icon");

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setIconImage(bubbleIcon.getImage());
        setBounds(100, 100, 270, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        // Configure the input panel
        nicknameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nicknameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        portLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        portField.setAlignmentX(Component.LEFT_ALIGNMENT);

        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(new EmptyBorder(10, 20, 0, 20));
        inputPanel.add(nicknameLabel);
        inputPanel.add(nicknameField);
        inputPanel.add(portLabel);
        inputPanel.add(portField);

        // Configure the dialog actions panel
        dialogActionsPanel.add(confirmButton);
        dialogActionsPanel.add(cancelButton);

        // Combine the input and actions panels
        inputActionPanel.setLayout(new BorderLayout(0, 0));
        inputActionPanel.add(inputPanel, BorderLayout.NORTH);
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
                if(validateFields()) {
                    setSettings();
                    dispose();
                }
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
     * Validates the data in the input fields
     *
     * @return
     *          Tells if the data is valid or not
     */
    protected boolean validateFields() {
        String nickname = nicknameField.getText();
        String port = portField.getText();

        if(nickname.length() == 0) {
            JOptionPane.showMessageDialog(null, "No nickname specified");
            return false;
        }

        if(port.length() == 0) {
            JOptionPane.showMessageDialog(null, "No port specified");
            return false;
        }

        int portInteger = 0;
        try {
             portInteger = Integer.parseInt(port);
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Port is not a valid integer");
            return false;
        }

        if(portInteger < 0 || portInteger > 65535) {
            JOptionPane.showMessageDialog(null, "Port can only be between 0 and 65535 inclusive");
            return false;
        }

        return true;
    }

    /**
     * Saves the settings collected from the dialog
     */
    protected void setSettings() {
        UserSettings.getInstance().setNickname(nicknameField.getText());
        UserSettings.getInstance().setPort(Integer.parseInt(portField.getText()));
    }
}
