/** InitialSettingsDialog.java */
package org.com1028.gui.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * Used to edit the application settings and open the main frame
 *
 * @author Emil Georgiev
 */
public class InitialSettingsDialog extends SettingsDialog {

    private static final long serialVersionUID = 6261754242229649378L;

    /** Creates the dialog */
    public InitialSettingsDialog(JFrame owner) {
        super(owner);
        confirmButton.removeActionListener(confirmButton.getActionListeners()[0]);
        confirmButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(validateFields()) {
                    setSettings();
                    dispose();
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                }
            }
        });
    }
}
