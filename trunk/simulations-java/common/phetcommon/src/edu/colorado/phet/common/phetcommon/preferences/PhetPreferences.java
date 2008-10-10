/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public class PhetPreferences {

    private static final String SEPARATOR = System.getProperty( "file.separator" );
    private static final File PREFERENCES_FILE = new File( System.getProperty( "user.home" ) + SEPARATOR + ".phet" + SEPARATOR + "preferences.properties" );
    
    private static final String KEY_UPDATES_ENABLED = "all-sims.updates.enabled";
    private static final String KEY_TRACKING_ENABLED = "all-sims.tracking.enabled";
    public static final String KEY_PREFERENCES_FILE_CREATION_TIME = "preferences-file-creation-time.milliseconds";
    private static final String PATTERN_KEY_ASK_ME_LATER = "{0}.{1}.updates.ask-me-later-pressed.milliseconds";
    private static final String PATTERN_KEY_SKIP_UPDATE = "{0}.{1}.updates.skip.version"; // project.sim.updates.skip-version
        
    private static PhetPreferences instance;
    
    private final Properties properties = new Properties();
    
    private PhetPreferences() {
        if ( !PREFERENCES_FILE.exists() ) {
            PREFERENCES_FILE.getParentFile().mkdirs();
            setPreferencesFileCreationTimeNow();
            setUpdatesEnabled( true );
            setTrackingEnabled( true );
            storePreferences();
        }
        try {
            properties.load( new FileInputStream( PREFERENCES_FILE ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    public static PhetPreferences getInstance() {
        if ( instance == null ) {
            instance = new PhetPreferences();
        }
        return instance;
    }

    public void setTrackingEnabled( boolean b ) {
        setBooleanProperty( KEY_TRACKING_ENABLED, b );
    }

    public boolean isTrackingEnabled() {
        return getBooleanProperty( KEY_TRACKING_ENABLED );
    }
    
    public void setUpdatesEnabled( boolean b ) {
        setBooleanProperty( KEY_UPDATES_ENABLED, b );
    }
    
    public boolean isUpdatesEnabled() {
        return getBooleanProperty( KEY_UPDATES_ENABLED );
    }
    
    /**
     * Sets the time in milliseconds since Epoch at which the user requested to be asked later.
     * This is specific to a simulation.
     */
    public void setAskMeLater( String project, String sim, long time ) {
        setStringProperty( getAskMeLaterKey( project, sim ), String.valueOf( time ) );
    }

    /**
     * Gets the time in milliseconds since Epoch at which the user requested to be asked later.
     * This is specific to a simulation.
     */
    public long getAskMeLater( String project, String sim ) {
        long timestamp = 0;
        try {
            String s = getStringProperty( getAskMeLaterKey( project, sim ) );
            if ( s != null ) {
                timestamp = Long.parseLong( s );
            }
        }
        catch ( NumberFormatException e ) {
            e.printStackTrace();
        }
        return timestamp;
    }
    
    private static String getAskMeLaterKey( String project, String sim ) {
        Object[] args = { project, sim };
        return MessageFormat.format( PATTERN_KEY_ASK_ME_LATER, args );
    }

    /**
     * Sets the most recent version that the user asked to skip.
     * This is specific to a simulation.
     */
    public void setSkipUpdate( String project, String sim, PhetVersion version ) {
        setStringProperty( getSkipUpdateKey( project, sim ), String.valueOf( version.getRevisionAsInt() ) );
    }

    /**
     * Gets the most recent version that the user asked to skip.
     * This is specific to a simulation.
     */
    public int getSkipUpdate( String project, String sim ) {
        int version = 0;
        try {
            String s = getStringProperty( getSkipUpdateKey( project, sim ) );
            if ( s != null ) {
                version = Integer.parseInt( s );
            }
        }
        catch ( NumberFormatException e ) {
            e.printStackTrace();
        }
        return version;
    }
    
    private static String getSkipUpdateKey( String project, String sim ) {
        Object[] args = { project, sim };
        return MessageFormat.format( PATTERN_KEY_SKIP_UPDATE, args );
    }
    
    /**
     * Sets the time in milliseconds since Epoch that the preferences file was created.
     * We use this as an ad hoc means of anonymously identifying unique users.
     */
    private void setPreferencesFileCreationTimeNow() {
        setStringProperty( KEY_PREFERENCES_FILE_CREATION_TIME, String.valueOf( System.currentTimeMillis() ) );
    }

    /**
     * Gets the time in milliseconds since Epoch that the preferences file was created.
     * We use this as an ad hoc means of anonymously identifying unique users.
     * If the file doesn't exist, it is created as a side effect.
     */
    public long getPreferencesFileCreatedAtMillis() {
        if ( properties.getProperty( KEY_PREFERENCES_FILE_CREATION_TIME ) == null ) {
            setPreferencesFileCreationTimeNow();
        }
        long timeStamp = -1;
        try {
            timeStamp = Long.parseLong( properties.getProperty( KEY_PREFERENCES_FILE_CREATION_TIME ) );
        }
        catch ( NumberFormatException e ) {
            e.printStackTrace();
        }
        return timeStamp;
    }
    
    private void setBooleanProperty( String key, boolean b ) {
        setStringProperty( key, String.valueOf( b ) );
    }
    
    private boolean getBooleanProperty( String key ) {
        return Boolean.valueOf( getStringProperty( key ) ).booleanValue(); // any value other than "true" is false
    }
    
    private void setStringProperty( String key, String value ) {
        properties.setProperty( key, value );
        storePreferences();
    }
    
    private String getStringProperty( String key ) {
        return properties.getProperty( key );
    }
    
    private void storePreferences() {
        try {
            properties.store( new FileOutputStream( PREFERENCES_FILE ), "Preferences for PhET, see http://phet.colorado.edu" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return properties.toString();
    }

    public static void main( String[] args ) throws IOException {
        PhetPreferences phetPreferences = new PhetPreferences();
        System.out.println( "phetPreferences = " + phetPreferences );
    }
}
