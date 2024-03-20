/** UserSettings.java */
package org.com1028.model;

import java.util.prefs.Preferences;

/**
 * A singleton class that's used to change the settings of the current user
 *
 * @author Emil Georgiev
 */
public class UserSettings {

    /** Holds the singleton instance of the class */
    private static UserSettings singleton;
    /** Holds the preferences object used to store and retrieve the settings */
    private final Preferences   settings;

    /**
     * Creates an instance of the class
     */
    private UserSettings() {
        settings = Preferences.userRoot().node(this.getClass().getName());
    }

    /**
     * Creates and returns the instance of the class
     *
     * @return
     *          The singleton instance
     */
    public static UserSettings getInstance() {
        if(singleton == null) {
            singleton = new UserSettings();
        }

        return singleton;
    }

    /**
     * Set the nickname of the current user
     *
     * @param nickname
     *          The nickname to be set
     */
    public void setNickname(String nickname) {
        this.settings.put("nickname", nickname);
    }

    /**
     * Set the port of the current user
     *
     * @param port
     *          The port ot be set
     */
    public void setPort(int port) {
        this.settings.putInt("port", port);
    }

    /**
     * Get the nickname of the current user
     *
     * @return
     *          The nickname
     */
    public String getNickname() {
        return this.settings.get("nickname", "DEFAULT_STRING");
    }

    /**
     * Get the port of the current user
     *
     * @return
     *          The port
     */
    public int getPort() {
        return this.settings.getInt("port", -1);
    }
}