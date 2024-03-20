/** StartConversationButton.java */
package org.com1028.gui.panel.conversations;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * Creates the button used to create new conversations
 *
 * @author Emil Georgiev
 */
public class StartConversationButton extends JButton {

    private static final long serialVersionUID = -5085459206395014754L;

    /**
     * Creates the button
     */
    public StartConversationButton() {
        ImageIcon plusIcon = new ImageIcon("images/plus_icon.png", "plus_icon");
        plusIcon = new ImageIcon(plusIcon.getImage().getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH));

        Border currentBorder = ((CompoundBorder)getBorder()).getOutsideBorder();
        Border padding       = new EmptyBorder(8, 8, 8, 8);

        setIcon(plusIcon);
        setText("Start Conversation");
        setBackground(Color.decode("0xEEEEEE"));
        setHorizontalAlignment(LEFT);

        setBorder(new CompoundBorder(currentBorder, padding));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
