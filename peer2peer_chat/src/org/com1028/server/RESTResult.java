/** RESTResult.java */
package org.com1028.server;

/**
 * Holds the result of a RESTHandler
 *
 * @author Emil Georgiev
 */
public class RESTResult {

    /** The response body */
    private String output = "";
    /** The HTTP response status code */
    private int    status = 500;

    /**
     * Creates the object
     *
     * @param output
     *          The response body
     * @param status
     *          The HTTP response status code
     */
    public RESTResult(String output, int status) {
        setOutput(output);
        setStatus(status);
    }

    /**
     * Get the response body
     *
     * @return
     *          The response body
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * Get the HTTP response status code
     *
     * @return
     *          The status code
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * Set the response body
     *
     * @param output
     *          The respone body to be set
     */
    private void setOutput(String output) {
        if(output == null) {
            throw new IllegalArgumentException("Error: output can't be null");
        }

        this.output = output;
    }

    /**
     * Set the HTTP response status code
     *
     * @param status
     *          The status code to be set
     */
    private void setStatus(int status) {
        if(status < 100 || status > 599) {
            throw new IllegalArgumentException("Error: HTTP status is invalid");
        }

        this.status = status;
    }
}
