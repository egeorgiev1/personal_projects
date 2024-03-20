/** MessageItemRenderer.java */
package org.com1028.gui.panel.messages;

import java.awt.Component;
import java.util.Calendar;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.com1028.model.Message;

/**
 * Sets the values of the message bubbles in the list
 *
 * @author Emil Georgiev
 */
public class MessageItemRenderer extends MessageItem implements ListCellRenderer<Message> {

    private static final long serialVersionUID = 2102587417118655012L;

    /**
     * Sets the values of the message bubble
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends Message> list, Message msg, int index,
        boolean isSelected, boolean cellHasFocus) {

        String content = msg.getContent();
        boolean isMine = msg.isMine();
        Calendar date  = msg.getDate();

        setMessage(content);
        setIsMine(isMine);
        setSendDate(date);

        return this;
    }
}
