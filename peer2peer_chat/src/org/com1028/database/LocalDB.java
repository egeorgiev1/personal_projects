/** LocalDB.java */
package org.com1028.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JOptionPane;

import org.com1028.model.Conversation;
import org.com1028.model.ConversationListItemData;
import org.com1028.model.Message;

/**
 * A singleton class that provides access to the database.
 *
 * @author Emil Georgiev
 */
public class LocalDB implements IConversationsStorage, IMessagesStorage {

    /** Stores the singleton instance */
    static private LocalDB singleton = null;

    /** Stores the database connection object */
    private Connection db;

    /**
     * Creates an instance of the class.
     */
    private LocalDB() {
        try {
            db = DriverManager
                .getConnection(
                    "jdbc:hsqldb:file:db_data/myDBfilestore;ifexists=true;shutdown=true",
                    "SA",
                    ""
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Couldn't create the database connection");
            System.exit(0);
        }
    }

    /**
     * Creates and returns the singleton instance of the class.
     *
     * @return The singleton instance.
     */
    public static LocalDB getInstance() {
        if(singleton == null) {
            singleton = new LocalDB();
        }

        return singleton;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int addMessage(Message message) {
        PreparedStatement statement = null;
        try {
            statement = db.prepareStatement(
                    "INSERT INTO messages (content, is_mine, conversation_id, date) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, message.getContent());
            statement.setBoolean(2, message.isMine());
            statement.setInt(3, message.getConversationId());
            statement.setTimestamp(4, new java.sql.Timestamp(message.getDate().getTime().getTime()));

            int rowsCount = statement.executeUpdate();

            if (rowsCount == 0) {
                throw new SQLException("Insertion unsuccessful");
            }

            try(ResultSet keys = statement.getGeneratedKeys()) {
                if(keys.next()) {
                    return (int)keys.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void editMessage(Message message) {
        PreparedStatement statement = null;
        try {
            statement = db.prepareStatement(
                    "UPDATE messages SET content = ? WHERE id=?"
            );

            statement.setString(1, message.getContent());
            statement.setInt(2, message.getId());

            int rowsCount = statement.executeUpdate();

            if (rowsCount == 0) {
                throw new SQLException("Update unsuccessful");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeMessage(Message message) { // to test
        PreparedStatement statement = null;
        try {
            statement = db.prepareStatement(
                    "DELETE FROM messages WHERE id=?"
            );

            statement.setInt(1,  message.getId());

            int rowsCount = statement.executeUpdate();

            if (rowsCount == 0) {
                throw new SQLException("Delete unsuccessful");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> getRange(int conversationId) {
        List<Message> messages = new ArrayList<Message>();
        PreparedStatement statement = null;
        try {
            statement = db.prepareStatement(
                    "SELECT * FROM messages WHERE conversation_id=? ORDER BY date DESC "
            );
            statement.setInt(1, conversationId);

            ResultSet result = statement.executeQuery();

            while(result.next()) {
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(result.getTimestamp("date").getTime());


                Message message = new Message(
                        result.getInt("id"),
                        conversationId,
                        result.getString("content"),
                        result.getBoolean("is_mine"),
                        date
                );

                messages.add(message);
            }

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public boolean addConversation(Conversation conversation) {
        PreparedStatement statement = null;
        try {
            statement = db.prepareStatement(
                    "INSERT INTO conversations (id, ip, port, nickname, missed_messages) VALUES (?, ?, ?, ?, ?)"
            );

            statement.setInt(1, conversation.getId());
            statement.setString(2, conversation.getIp());
            statement.setInt(3, conversation.getPort());
            statement.setString(4, conversation.getNickname());
            statement.setInt(5, conversation.getMissedMessages());

            int rowsCount = statement.executeUpdate();

            if (rowsCount == 0) {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void editConversation(Conversation conversation) {
        PreparedStatement statement = null;
        try {
            statement = db.prepareStatement(
                    "UPDATE conversations SET nickname = ?, missed_messages = ? WHERE id=?"
            );

            statement.setString(1, conversation.getNickname());
            statement.setInt(2, conversation.getMissedMessages());
            statement.setInt(3, conversation.getId());

            int rowsCount = statement.executeUpdate();

            if (rowsCount == 0) {
                throw new SQLException("Edit unsuccessful");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeConversation(Conversation conversation) {
        PreparedStatement statement = null;
        try {
            statement = db.prepareStatement(
                    "DELETE FROM conversations WHERE id=?"
            );

            statement.setInt(1,  conversation.getId());

            int rowsCount = statement.executeUpdate();

            if (rowsCount == 0) {
                throw new SQLException("Delete unsuccessful");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ConversationListItemData> getRange() {
        List<ConversationListItemData> conversationItems = new ArrayList<ConversationListItemData>();
        PreparedStatement statement = null;
        try {
            statement = db.prepareStatement(""
                    + "SELECT conversations.*, t.* FROM conversations "
                    + "INNER JOIN ("
                        + "SELECT m1.* FROM messages m1 "
                        + "LEFT OUTER JOIN messages m2 ON m1.conversation_id=m2.conversation_id AND m1.date < m2.date "
                        + "WHERE m2.conversation_id IS NULL) t "
                    + "ON t.conversation_id=conversations.id "
                    + "ORDER BY date DESC "
            );

            ResultSet result = statement.executeQuery();
            while(result.next()) {
                Conversation conv = new Conversation(
                        result.getInt("id"),
                        result.getString("ip"),
                        result.getInt("port"),
                        result.getString("nickname"),
                        result.getInt("missed_messages")
                );

                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(result.getTimestamp("date").getTime());

                ConversationListItemData item = new ConversationListItemData(
                        conv,
                        result.getString("content"),
                        date
                );

                conversationItems.add(item);
            }

            return conversationItems;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversationItems;
    }

    /**
     * Empties the database.
     */
    public void emptyDB() {
        try {
            Statement stmt  = db.createStatement();
            stmt.execute("TRUNCATE TABLE messages");
            stmt.execute("TRUNCATE TABLE conversations");
            stmt.executeQuery("ALTER TABLE PUBLIC.MESSAGES ALTER COLUMN ID RESTART WITH 1");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the contents of the database.
     */
    public void printDB() {
        System.out.println("///////////////////////////////////////////////");
        Statement stmt = null;
        try {
            stmt = db.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM messages");

            while(result.next()) {
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(result.getTimestamp("date").getTime());

                Message message = new Message(
                        result.getInt("id"),
                        result.getInt("conversation_id"),
                        result.getString("content"),
                        result.getBoolean("is_mine"),
                        date
                );

                System.out.println(message);
            }

            System.out.println("");

            result = stmt.executeQuery("SELECT * FROM conversations");

            while(result.next()) {
                Conversation conversation = new Conversation(
                        result.getInt("id"),
                        result.getString("ip"),
                        result.getInt("port"),
                        result.getString("nickname"),
                        result.getInt("missed_messages")
                );

                System.out.println(conversation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("///////////////////////////////////////////////");
    }

}
