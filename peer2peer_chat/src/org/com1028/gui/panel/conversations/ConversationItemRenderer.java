/** ConversationItemRenderer.java */
package org.com1028.gui.panel.conversations;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.com1028.model.ConversationListItemData;

/**
 * Used by the JList to set the values of the conversation list item
 *
 * @author Emil Georgiev
 */
public class ConversationItemRenderer extends ConversationItem implements ListCellRenderer<ConversationListItemData> {

    private static final long serialVersionUID = -440831112393939033L;

    /**
     * Sets the values of the conversation list item.
     */
    @Override
    public Component getListCellRendererComponent(JList<? extends ConversationListItemData> list, ConversationListItemData conversation, int index,
        boolean isSelected, boolean cellHasFocus) {

        String partnerName = conversation.getConversation().getNickname();
        String lastMessage = conversation.getRecentMessageContent();

        setPartnerName(partnerName);
        setLastMessage(lastMessage);

        if(isSelected)
            setBackground(Color.decode("0xDDDDFF"));
        else
            setBackground(Color.WHITE);

        return this;
    }
}
