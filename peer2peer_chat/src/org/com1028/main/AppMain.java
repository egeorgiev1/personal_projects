/** AppMain.java */
package org.com1028.main;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.com1028.gui.frames.InitialSettingsDialog;

/**
 * The class, from which the application starts. Opens the initial settings dialog.
 *
 * @author Emil Georgiev
 */
public class AppMain {
    /**
     * The method, from which the applicaion starts.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JDialog initial = new InitialSettingsDialog(null);
                initial.setVisible(true);
            }
        });
    }
}