/** CreateConversationHandler.java */
package org.com1028.server;

import java.util.Calendar;
import java.util.Map;

import org.com1028.database.LocalDB;
import org.com1028.model.Conversation;
import org.com1028.model.ConversationListItemData;
import org.com1028.model.Message;
import org.com1028.model.UserSettings;

/**
 * A singleton handler that handlers the create conversation requests from other peers
 *
 * @author Emil Georgiev
 */
public class CreateConversationHandler extends RESTHandler {

    /** The singleton instance of the handler */
    private static CreateConversationHandler singleton = null;

    /**
     * Create the handler
     */
    private CreateConversationHandler() {
        super();
    }

    /**
     * Create and return the singleton instance
     *
     * @return
     *          The singleton instance
     */
    public static CreateConversationHandler getInstance() {
        if(singleton == null) {
            singleton = new CreateConversationHandler();
        }

        return singleton;
    }

    /**
     * Handle the create conversation request
     */
    @Override
    public RESTResult handleRequest(Map<String, String> query, String remoteAddress) {

        // Add content and date for the first message
        int      id       = Integer.parseInt(query.get("id"));
        String   ip       = remoteAddress; // ipv6 check???
        int      port     = Integer.parseInt(query.get("port"));
        String   nickname = query.get("nickname");
        String   content  = query.get("content");
        Calendar date     = Calendar.getInstance();

        long timestamp = Long.parseLong(query.get("date"));
        date.setTimeInMillis(timestamp);

        Conversation convForDB = new Conversation(
                id,
                ip,
                port,
                nickname,
                1
        );
        ConversationListItemData item = new ConversationListItemData(convForDB, content, date);
        boolean success = LocalDB.getInstance().addConversation(convForDB);

        if(!success) {
            return new RESTResult("1", 400);
        }

        Message msgForDB = new Message(
                id,
                content,
                false,
                date
        );

        int messageId = LocalDB.getInstance().addMessage(msgForDB);

        if(messageId == -1) {
            return new RESTResult("1", 400);
        }

        this.setChanged();
        this.notifyObservers(item);

        int status = 200;
        String response = UserSettings.getInstance().getNickname();

        return new RESTResult(response, status);
    }
}