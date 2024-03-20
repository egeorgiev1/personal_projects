/** ChatServer.java */
package org.com1028.server;

import java.io.FileInputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.JOptionPane;

import org.com1028.model.UserSettings;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

/**
 * Creates a singleton instance of the HTTPS server
 *
 * @author Emil Georgiev
 */
public class ChatServer {

    /** The singleton instance of the class */
    private static ChatServer singleton;

    /**
     * Creates an instance of the class
     */
    private ChatServer() {
        try
        {
            InetSocketAddress address = new InetSocketAddress ("0.0.0.0", UserSettings.getInstance().getPort());

            // Initialise the server
            HttpsServer server = HttpsServer.create(address, 0);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Initialize the keystore
            char[] password = "ILikeNekomimi123".toCharArray();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            FileInputStream inputStream = new FileInputStream("key/testkey.jks");
            keyStore.load(inputStream, password);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, password);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);

            // Configure the HTTPS server
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            server.setHttpsConfigurator (
                new HttpsConfigurator(sslContext) {
                    public void configure(HttpsParameters params) {
                        try {
                            SSLContext context = SSLContext.getDefault();
                            SSLEngine engine = context.createSSLEngine();

                            params.setNeedClientAuth(false);
                            params.setCipherSuites(engine.getEnabledCipherSuites());
                            params.setProtocols(engine.getEnabledProtocols());

                            params.setSSLParameters(context.getDefaultSSLParameters());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            });

            server.createContext("/send_message", ReceiveMessageHandler.getInstance());
            server.createContext("/create_conversation", CreateConversationHandler.getInstance()); // vrashta nickname, no ne i remote_id na initial message, moze i create message i initial message da stanat sas 2 requests kato vtoria e za sendmessagehandler, prosto normalen message???, no kak da se garantira, che shte se handlene predi sazdavaneto na samia conversation?!

            server.start();

        } catch (BindException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can't create server on port " + UserSettings.getInstance().getPort());
            System.exit(0);
        } catch(Exception e) {
            e.printStackTrace(System.out);
            JOptionPane.showMessageDialog(null, "Couldn't start server because of an unknown error");
            System.exit(0);
        }
    }

    /**
     * Creates and returns the singleton instance of the class
     *
     * @return
     *          The singleton instance
     */
    public static ChatServer getInstance() {
        if(singleton == null) {
            singleton = new ChatServer();
        }

        return singleton;
    }
}
