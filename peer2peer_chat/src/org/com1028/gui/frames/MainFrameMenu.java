/** MainFrameMenu.java */
package org.com1028.gui.frames;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Represents the menu at the top of the main frame
 *
 * @author Emil Georgiev
 */
public class MainFrameMenu extends JMenuBar {

    private static final long serialVersionUID = 7422972389008917289L;

    /** The file menu */
    private JMenu     fileMenu     = new JMenu("File");
    /** Opens the settings dialog */
    private JMenuItem settingsItem = new JMenuItem("Settings");
    /** Closes the application */
    private JMenuItem exitItem     = new JMenuItem("Exit");

    /** The help menu */
    private JMenu     helpMenu  = new JMenu("Help");
    /** Opens the about dialog */
    private JMenuItem aboutItem = new JMenuItem("About");

    /**
     * Creates the menu
     *
     * @param owner
     *          The frame, on which the menu is going to be displayed.
     */
    public MainFrameMenu(JFrame owner) {
        add(fileMenu);
        fileMenu.add(settingsItem);
        fileMenu.add(exitItem);

        add(helpMenu);
        helpMenu.add(aboutItem);

        fileMenu.setPreferredSize(new Dimension(33, 25));

        settingsItem.setPreferredSize(new Dimension(150, 25));
        exitItem.setPreferredSize(new Dimension(150, 25));
        aboutItem.setPreferredSize(new Dimension(150, 25));

        settingsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsDialog dialog = new SettingsDialog(owner);
                dialog.setVisible(true);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog dialog = new AboutDialog(owner);
                dialog.setVisible(true);
            }
        });
    }
}
