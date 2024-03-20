/** JVerticalScrollableList.java */
package org.com1028.gui.panel.messages;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.Scrollable;

/**
 * A list that can only be scrolled vertically and fits horizontally it's parent
 *
 * @author Emil Georgiev
 */
public class JVerticalScrollableList<T> extends JList<T> implements Scrollable {

    private static final long serialVersionUID = -8754211496723644733L;

    public JVerticalScrollableList(DefaultListModel<T> model) {
        super(model);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle rect, int orientation, int dir) {
        return 32;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle rect, int orientation, int dir) {
        return 32;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

}
