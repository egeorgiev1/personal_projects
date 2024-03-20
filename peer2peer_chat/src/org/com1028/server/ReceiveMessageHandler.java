/** ReceiveMessageHandler.java */
package org.com1028.server;

import java.util.Calendar;
import java.util.Map;

import org.com1028.database.LocalDB;
import org.com1028.main.TaskQueue;
import org.com1028.model.Message;

/**
 * A singleton handler that handles messages, that have been sent by other peers to the current peer
 *
 * @author Emil Georgiev
 */
public class ReceiveMessageHandler extends RESTHandler {

    /** The singleton instance of the class */
    private static ReceiveMessageHandler singleton = null;

    /**
     * Creates the handler
     */
    private ReceiveMessageHandler() {
        super();
    }

    /**
     * Creates and returns the singleton instance
     *
     * @return
     *          The singleton instance
     */
    public static ReceiveMessageHandler getInstance() {
        if(singleton == null) {
            singleton = new ReceiveMessageHandler();
        }

        return singleton;
    }

    /**
     * Handle the request
     */
    @Override
    public RESTResult handleRequest(Map<String, String> query, String remoteAddress) {

        int conversationId = Integer.parseInt(query.get("conversationid"));
        String content = query.get("content");
        Calendar date = Calendar.getInstance();
        long timestamp = Long.parseLong(query.get("date"));
        date.setTimeInMillis(timestamp);

        Message msgForDB = new Message(
            conversationId,
            content,
            false,
            date
        );

        int id = LocalDB.getInstance().addMessage(msgForDB);

        if(id == -1) {
            return new RESTResult("1", 500);
        }

        TaskQueue.addTask(new Runnable() {
            @Override
            public void run() {
                ReceiveMessageHandler.this.setChanged();
                ReceiveMessageHandler.this.notifyObservers(msgForDB);
            }
        });

        int status = 200;
        String response = Integer.toString(id);

        return new RESTResult(response, status);
    }
}