package edu.colorado.phet.unfuddletool;

public class Configuration {

    public static String getAccountName() {
        return "phet";
    }

    public static int getProjectId() {
        return 9404;
    }

    public static String browser() {
        return "firefox";
    }

    public static String getProjectIdString() {
        return String.valueOf( getProjectId() );
    }

    public static String getDefaultHTMLFont() {
        return "BitStream Vera Serif";
    }
}
