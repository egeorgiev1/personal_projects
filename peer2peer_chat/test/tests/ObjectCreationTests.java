package tests;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.DefaultListModel;

import org.com1028.database.LocalDB;
import org.com1028.gui.frames.AboutDialog;
import org.com1028.gui.frames.CreateConversationDialog;
import org.com1028.gui.panel.conversations.ConversationItemRenderer;
import org.com1028.gui.panel.conversations.ConversationsPanel;
import org.com1028.gui.panel.conversations.StartConversationButton;
import org.com1028.gui.panel.messages.JVerticalScrollableList;
import org.com1028.gui.panel.messages.MessageActionsMenu;
import org.com1028.gui.panel.messages.MessageItem;
import org.com1028.gui.panel.messages.MessageItemRenderer;
import org.com1028.gui.panel.messages.MessagesPanel;
import org.com1028.gui.panel.messages.SendMessagePanel;
import org.com1028.main.AppMain;
import org.com1028.main.TaskQueue;
import org.com1028.model.Conversation;
import org.com1028.model.ConversationListItemData;
import org.com1028.model.ConversationListModel;
import org.com1028.model.Message;
import org.com1028.model.OpenConversationModel;
import org.com1028.model.UserSettings;
import org.com1028.server.ChatServer;
import org.com1028.server.CreateConversationHandler;
import org.com1028.server.HttpRequest;
import org.com1028.server.RESTResult;
import org.com1028.server.ReceiveMessageHandler;
import org.junit.Test;

public class ObjectCreationTests {

    @Test
    public void databaseCreation() {
        LocalDB.getInstance();
    }

    @Test
    public void aboutDialogCreation() {
        new AboutDialog(null);
    }

    @Test
    public void createConversationCreation() {
        new CreateConversationDialog(null, new ConversationListModel());
    }

    @Test
    public void conversationItemRendererCreation() {
        new ConversationItemRenderer();
    }

    @Test
    public void conversationsPanelCreation() {
        new ConversationsPanel(new ConversationListModel(), new OpenConversationModel());
    }

    @Test
    public void startConversationButtonCreation() {
        new StartConversationButton();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void verticalScrollableListCreation() {
        new JVerticalScrollableList(new DefaultListModel());
    }

    @Test
    public void messagesActionsMenuCreation() {
        new MessageActionsMenu();
    }

    @Test
    public void messageItemCreation() {
        new MessageItem();
    }

    @Test
    public void messageItemRendererCreation() {
        new MessageItemRenderer();
    }

    @Test
    public void messagesPanelCreation() {
        new MessagesPanel(new OpenConversationModel());
    }

    @Test
    public void sendMessagePanelCreation() {
        new SendMessagePanel(new OpenConversationModel());
    }

    @Test
    public void appMainCreation() {
        new AppMain();
    }

    @Test
    public void taskQueueCreation() {
        new TaskQueue();
    }

    @Test
    public void conversationCreation() {
        new Conversation("0.0.0.0", 123, "nickname", 13);
    }

    @Test
    public void conversationListItemDataCreation() {
        new ConversationListItemData(
                new Conversation("0.0.0.0", 123, "nickname", 13),
                "content",
                Calendar.getInstance()
        );
    }

    @Test
    public void conversationListModelCreation() {
        new ConversationListModel();
    }

    @Test
    public void messageCreation() {
        new Message(123, "content", true, Calendar.getInstance());
    }

    @Test
    public void openConversationModelCreation() {
        new OpenConversationModel();
    }

    @Test
    public void userSettingsCreation() {
        UserSettings.getInstance();
    }

    @Test
    public void chatServerCreation() {
        ChatServer.getInstance();
    }

    @Test
    public void createConversationHandlerCreation() {
        CreateConversationHandler.getInstance();
    }

    @Test
    public void httpRequestCreation() {
        try {
            new HttpRequest("localhost", "blabla", new HashMap<String, String>());
        } catch (SocketTimeoutException e) {
        } catch (ConnectException e) {
        } catch(Exception e) {
        }
    }

    @Test
    public void receiveMessageHandlerCreation() {
        ReceiveMessageHandler.getInstance();
    }

    @Test
    public void restResultCreation() {
        new RESTResult("output", 123);
    }
}
