/** AboutDialog.java */
package org.com1028.gui.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Shows information about the application.
 *
 * @author Emil Georgiev
 */
public class AboutDialog extends JDialog {

    private static final long serialVersionUID = -1018422780290986102L;

    /** Holds the content of the dialog */
    private JPanel contentPanel = new JPanel();
    /** Holds the title of the dialog on the top of the GUI */
    private JLabel titleLabel   = new JLabel("About");

    /** Holds the actual information of the application */
    private JPanel aboutPanel    = new JPanel();
    /** Shows the application icon */
    private JLabel programIcon   = new JLabel();
    /** Shows the name of the application */
    private JLabel appName       = new JLabel("Bubble");
    /** Shows the current version of the application */
    private JLabel appVersion    = new JLabel("Version: 1.0");
    /** Shows the name of the developer of the application */
    private JLabel developerName = new JLabel("Developer: Emil Georgiev");

    /**
     * Creates an instance of the dialog.
     *
     * @param owner
     *          The window that has created this dialog.
     */
    public AboutDialog(Window owner) {
        super(owner, "Bubble - About", Dialog.ModalityType.APPLICATION_MODAL);

        // Configure the frame
        ImageIcon bubbleIcon = new ImageIcon("images/bubble_icon.png", "bubble_icon");

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setIconImage(bubbleIcon.getImage());
        setBounds(100, 100, 270, 190);
        setLocationRelativeTo(null);
        setResizable(false);

        // Configure the about panel
        aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
        aboutPanel.setBorder(new EmptyBorder(10, 20, 0, 20));
        aboutPanel.add(programIcon);
        aboutPanel.add(appName);
        aboutPanel.add(appVersion);
        aboutPanel.add(developerName);

        programIcon.setIcon(bubbleIcon);

        programIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);
        appVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        developerName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create a label above the input fields holding the dialog name
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog.Bold", Font.PLAIN, 15));

        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(aboutPanel, BorderLayout.CENTER);

        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });

        setContentPane(contentPanel);
    }
}
