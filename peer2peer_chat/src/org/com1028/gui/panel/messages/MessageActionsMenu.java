/** MessageActionsMenu.java */
package org.com1028.gui.panel.messages;

import java.awt.Dimension;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Represents the context menu for editing and deleting messages
 *
 * @author Emil Georgiev
 */
public class MessageActionsMenu extends JPopupMenu {

    private static final long serialVersionUID = -421301359063558620L;

    /** Opens a dialog to edit the message */
    private JMenuItem editItem   = new JMenuItem("Edit");
    /** Opens a dialog to delete the message */
    private JMenuItem deleteItem = new JMenuItem("Delete");

    /**
     * Creates the context menu
     */
    public MessageActionsMenu() {
        editItem.setPreferredSize(new Dimension(150, 25));
        deleteItem.setPreferredSize(new Dimension(150, 25));

        add(editItem);
        add(deleteItem);
    }
}
