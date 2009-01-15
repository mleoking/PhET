/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessControlException;
import java.text.MessageFormat;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.application.PhetPersistenceDir;

/**
 * PhET preferences, stored in a file in the user's home directory.
 */
public class PhetPreferences {

    // if we don't have access permissions, this file will be null 
    private static File PREFERENCES_FILE;
    static {
        try {
            PREFERENCES_FILE = new PhetPreferencesFile();
        }
        catch( AccessControlException accessControlException ) {
            PREFERENCES_FILE = null;
            System.out.println( "PhetPreferences: access to local filesystem denied" );
        }
    }
    
    /**
     * Preferences are stored in a file in the persistence directory.
     */
    private static class PhetPreferencesFile extends File {
        public PhetPreferencesFile() throws AccessControlException {
            super( new PhetPersistenceDir(), "preferences.properties" );
        }
    }

    // property keys
    public static final String KEY_PREFERENCES_FILE_CREATION_TIME = "preferences-file-creation-time.milliseconds";
    private static final String KEY_UPDATES_ENABLED = "all-sims.updates.enabled";
    private static final String KEY_TRACKING_ENABLED = "all-sims.tracking.enabled";
    private static final String KEY_SOFTWARE_AGREEMENT_VERSION = "all-sims.software-agreement-version";
    
    // property key patterns
    private static final String PATTERN_KEY_ASK_ME_LATER = "{0}.{1}.updates.ask-me-later-pressed.milliseconds";
    private static final String PATTERN_KEY_SKIP_UPDATE = "{0}.{1}.updates.skip.version"; // project.sim.updates.skip-version
    
    // developer only
    private static final String DEV_KEY_ALWAYS_SHOW_SOFTWARE_AGREEMENT = "dev.always-show-software-agreement";

    /* singleton */
    private static PhetPreferences instance;

    private final Properties properties = new Properties();

    /* singleton */
    private PhetPreferences() {
        if ( PREFERENCES_FILE == null ) {
            //can't read or write properties due to access control exception
            setDefaults();
        }
        else {
            if ( !PREFERENCES_FILE.exists() ) {
                PREFERENCES_FILE.getParentFile().mkdirs();
                setDefaults();
                storePreferences();
            }
            try {
                properties.load( new FileInputStream( PREFERENCES_FILE ) );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private void setDefaults() {
        setPreferencesFileCreationTimeNow();
        setUpdatesEnabled( true );
        setTrackingEnabled( true );
        setAlwaysShowSoftwareAgreement( false );
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
        catch( NumberFormatException e ) {
            e.printStackTrace();
        }
        return timestamp;
    }

    private static String getAskMeLaterKey( String project, String sim ) {
        Object[] args = {project, sim};
        return MessageFormat.format( PATTERN_KEY_ASK_ME_LATER, args );
    }

    /**
     * Sets the most recent version that the user asked to skip.
     * This is specific to a simulation.
     */
    public void setSkipUpdate( String project, String sim, int skipRevision ) {
        setStringProperty( getSkipUpdateKey( project, sim ), String.valueOf( skipRevision ) );
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
        catch( NumberFormatException e ) {
            e.printStackTrace();
        }
        return version;
    }

    private static String getSkipUpdateKey( String project, String sim ) {
        Object[] args = {project, sim};
        return MessageFormat.format( PATTERN_KEY_SKIP_UPDATE, args );
    }

    public void setSoftwareAgreementVersion( int version ) {
        setIntProperty( KEY_SOFTWARE_AGREEMENT_VERSION, version );
    }
    
    /**
     * Gets the software agreement version number that the user most recently accepted.
     * @return -1 if the user has no accepted any agreement
     */
    public int getSoftwareAgreementVersion() {
        return getIntProperty( KEY_SOFTWARE_AGREEMENT_VERSION, -1 );
    }
    
    /**
     * Sets the time in milliseconds since Epoch that the preferences file was created.
     * We use this as an ad hoc means of anonymously identifying unique users.
     */
    private long setPreferencesFileCreationTimeNow() {
        long now = System.currentTimeMillis();
        setLongProperty( KEY_PREFERENCES_FILE_CREATION_TIME, now );
        return now;
    }
    
    public boolean isAlwaysShowSoftwareAgreement() {
        return getBooleanProperty( DEV_KEY_ALWAYS_SHOW_SOFTWARE_AGREEMENT );
    }
    
    public void setAlwaysShowSoftwareAgreement( boolean b ) {
        setBooleanProperty( DEV_KEY_ALWAYS_SHOW_SOFTWARE_AGREEMENT, b );
    }

    /**
     * Gets the time in milliseconds since Epoch that the preferences file was created.
     * We use this as an ad hoc means of anonymously identifying unique users.
     * If the file doesn't exist, it is created as a side effect.
     */
    public long getPreferencesFileCreationTime() {
        final long defaultValue = -1;
        long value = getLongProperty( KEY_PREFERENCES_FILE_CREATION_TIME, defaultValue );
        if ( value == defaultValue ) {
            value = setPreferencesFileCreationTimeNow();
        }
        return value;
    }
    
    /*
     * All other property setters should be implemented in terms of this one,
     * since this is the one that actually handles storage of the property.
     */
    private void setStringProperty( String key, String value ) {
        properties.setProperty( key, value );
        storePreferences();
    }

    private String getStringProperty( String key ) {
        return properties.getProperty( key );
    }

    private void setBooleanProperty( String key, boolean b ) {
        setStringProperty( key, String.valueOf( b ) );
    }

    private boolean getBooleanProperty( String key ) {
        return Boolean.valueOf( getStringProperty( key ) ).booleanValue(); // any value other than "true" is false
    }

    private void setLongProperty( String key, long value ) {
        setStringProperty( key, String.valueOf( value ) );
    }
    
    private long getLongProperty( String key, long defaultValue ) {
        long value = defaultValue;
        String stringValue = properties.getProperty( key );
        if ( stringValue != null ) {
            try {
                value = Long.parseLong( properties.getProperty( key ) );
            }
            catch ( NumberFormatException e ) {
                e.printStackTrace();
            }
        }
        return value;
    }
    
    private void setIntProperty( String key, int value ) {
        setStringProperty( key, String.valueOf( value ) );
    }
    
    private int getIntProperty( String key, int defaultValue ) {
        int value = defaultValue;
        String stringValue = properties.getProperty( key );
        if ( stringValue != null ) {
            try {
                value = Integer.parseInt( properties.getProperty( key ) );
            }
            catch ( NumberFormatException e ) {
                e.printStackTrace();
            }
        }
        return value;
    }

    private void storePreferences() {
        if ( PREFERENCES_FILE != null ) {
            try {
                properties.store( new FileOutputStream( PREFERENCES_FILE ), "Preferences for PhET, see " + PhetCommonConstants.PHET_HOME_URL );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
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
