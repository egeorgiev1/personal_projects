/** RESTHandler.java */
package org.com1028.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.JOptionPane;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * A convenience class that extracts data for the concrete handlers and provides a convenient interface
 * for them give the response
 *
 * @author Emil Georgiev
 */
public abstract class RESTHandler extends Observable implements HttpHandler {

    /**
     * Finds the remote address, parses the URL query and gives this information
     * to the concrete handler
     */
    @Override
    public final void handle(HttpExchange exchange) throws IOException {

        // Get the remote address
        String remoteAddress = "";
        Headers headers = exchange.getRequestHeaders();
        List<String> headerValue = headers.get("X-Forwarded-For");
        if (headerValue == null) {
            remoteAddress = exchange.getRemoteAddress().getAddress().getHostAddress();
        } else {
            remoteAddress = headerValue.get(0);
        }

        // Parse the query part of the URL
        Map<String, String> query = new HashMap<String, String>();
        try {
            URI uri = exchange.getRequestURI();
            if(uri.getQuery() != null) {
                for(String s: uri.getQuery().split("&")) {
                    String key = s.split("=")[0];
                    String val = URLDecoder.decode(s.split("=")[1], "UTF-8");;

                    query.put(key, val);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An unexpected " + e.getClass().getName() + " occured");
            System.exit(0);
        }

        // Process the request and get the resulting response string and status
        RESTResult output = this.handleRequest(query, remoteAddress);

        // Send the response
        String response = output.getOutput();
        int    status   = output.getStatus();

        exchange.sendResponseHeaders(status, response.length());
        OutputStream out = exchange.getResponseBody();
        out.write(response.getBytes());

        exchange.close();
    }

    /**
     * The actual handler of the request
     *
     * @param query
     *          The URL query in a convenient format
     * @param remoteAddress
     *          The remote address
     * @return
     *          The response body and status code
     */
    public abstract RESTResult handleRequest(Map<String, String> query, String remoteAddress);
}