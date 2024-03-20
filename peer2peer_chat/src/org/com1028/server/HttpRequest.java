/** HttpRequest.java */
package org.com1028.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;

/**
 * Used to create HTTP request with a convenient interface for the application
 *
 * @author Emil Georgiev
 */
public class HttpRequest {

    /** The HTTP response status code */
    private int    status;
    /** The response body */
    private String responseBody;

    /**
     * Creates the HttpRequest and handles the response
     *
     * @param hostname
     *          The hostname of the request
     * @param dir
     *          The REST request
     * @param query
     *          The query used to input the REST parameters
     * @throws SocketTimeoutException
     * @throws ConnectException
     */
    public HttpRequest(String hostname, String dir, Map<String, String> query) throws SocketTimeoutException, ConnectException {

        // Disable certificate validation for the acceptance of self-signed certificates
        TrustManager[] naiveTrustManager = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
                public void checkClientTrusted(X509Certificate[] certificates, String authenticationType) {}
                public void checkServerTrusted(X509Certificate[] certificates, String authenticationType) {}
            }
        };

        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, naiveTrustManager, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch(KeyManagementException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An unexpected " + e.getClass().getName() + " occured");
            System.exit(0);
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An unexpected " + e.getClass().getName() + " occured");
            System.exit(0);
        }

        // Create the URL
        String url = null;
        try {
            StringBuilder queryString = new StringBuilder();
            Iterator<Entry<String, String>> it = query.entrySet().iterator();
            while(it.hasNext()) {
                Entry<String, String> entry = it.next();

                queryString.append(entry.getKey());
                queryString.append("=");

                queryString.append(
                        URLEncoder.encode(
                                entry.getValue(),
                                StandardCharsets.UTF_8.toString()
                        )
                );

                if(it.hasNext()) {
                    queryString.append("&");
                }
            }

            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://");
            urlBuilder.append(hostname);
            urlBuilder.append("/");
            urlBuilder.append(dir);
            if(queryString.length() > 0) {
                urlBuilder.append("?");
                urlBuilder.append(queryString);
            }

            url = urlBuilder.toString();
        } catch(IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An unexpected " + e.getClass().getName() + " occured");
            System.exit(0);
        }

        // Create the HTTP connection
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setDefaultUseCaches(false);
            connection.setConnectTimeout(5000);

            // Bypass the server identity check
            final String urlForVerifier = url;
            connection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    try {
                        if(hostname.equals(new URL(urlForVerifier).getHost())) {
                            return true;
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "An unexpected " + e.getClass().getName() + " occured");
                        System.exit(0);
                    }
                    return false;
                }
            });

            @SuppressWarnings("resource")
            Scanner streamScanner = new Scanner(connection.getInputStream()).useDelimiter("\\A");
            String remoteIdString = streamScanner.hasNext() ? streamScanner.next() : "";

            this.status = connection.getResponseCode();
            this.responseBody = remoteIdString;

        } catch(SocketTimeoutException e) {
            throw e;
        } catch(ConnectException e) {
            throw e;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An unexpected " + e.getClass().getName() + " occured");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An unexpected " + e.getClass().getName() + " occured");
            System.exit(0);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Returns the HTTP response status code
     *
     * @return
     *          The status code
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * Returns the HTTP response body
     *
     * @return
     *          The response body
     */
    public String getResponseBody() {
        return this.responseBody;
    }
}
