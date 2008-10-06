package edu.colorado.phet.common.phetcommon.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public class PhetPreferences {
    private Properties properties = new Properties();

    private static PhetPreferences instance;
    private static final String ALL_SIMS = "all-sims";
    private static final String TRACKING = "tracking";
    private static final String APPLY_ALL = "applyToAll";
    private static final String DOT = ".";
    private static final String UPDATES = "updates";
    private static final String SEPARATOR = System.getProperty( "file.separator" );
    private static final File PREFERENCES_FILE = new File( System.getProperty( "user.home" ) + SEPARATOR + ".phet" + SEPARATOR + "preferences.properties" );
    private static final String TRACKING_APPLY_TO_ALL = ALL_SIMS + DOT + TRACKING + DOT + APPLY_ALL;
    private static final String UPDATES_APPLY_TO_ALL = ALL_SIMS + DOT + UPDATES + DOT + APPLY_ALL;
    private static final String LAST_ASK_ME_LATER_TIME = "updates.last-time-ask-me-later-pressed.system-time-millis";

    private PhetPreferences() {
        if ( !PREFERENCES_FILE.exists() ) {
            PREFERENCES_FILE.getParentFile().mkdirs();

            setUpdatesEnabledForAll( true );
            setApplyUpdatesToAll( true );

            setTrackingEnabledForAll( true );
            setApplyTrackingToAll( true );

            storePreferences();
        }
        try {
            properties.load( new FileInputStream( PREFERENCES_FILE ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void storePreferences() {
        try {
            properties.store( new FileOutputStream( PREFERENCES_FILE ), "Preferences for PhET, see http://phet.colorado.edu" );
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

    public void setApplyTrackingToAll( boolean applyAll ) {
        properties.setProperty( TRACKING_APPLY_TO_ALL, Boolean.toString( applyAll ) );
        storePreferences();
    }

    public boolean isTrackingApplyToAll() {
        String s = properties.getProperty( TRACKING_APPLY_TO_ALL, "true" );
        return Boolean.valueOf( s ).booleanValue();
    }

    public void setApplyUpdatesToAll( boolean applyAll ) {
        properties.setProperty( UPDATES_APPLY_TO_ALL, Boolean.toString( applyAll ) );
        storePreferences();
    }

    public boolean isUpdatesApplyToAll() {
        String updatesApplyAll = properties.getProperty( UPDATES_APPLY_TO_ALL, "true" );
        return Boolean.valueOf( updatesApplyAll ).booleanValue();
    }

    public void setTrackingEnabledForAll( boolean b ) {
        setBooleanEnabled( ALL_SIMS, TRACKING, b );
    }

    public void setUpdatesEnabledForAll( boolean b ) {
        setBooleanEnabled( ALL_SIMS, UPDATES, b );
    }

    public boolean isTrackingEnabled( String project, String sim ) {
        if ( isTrackingApplyToAll() ) {
            return isTrackingEnabledForAll();
        }
        else {
            return getBooleanEnabled( project, sim, TRACKING,
                                      //if on a simulation by simulation basis, and no value specified, use the last value specified for all-sims
                                      isTrackingEnabledForAll() );
        }
    }

    public boolean isUpdatesEnabled( String project, String sim ) {
        if ( isUpdatesApplyToAll() ) {
            return isUpdatesEnabledForAll();
        }
        else {

            return getBooleanEnabled( project, sim, UPDATES,
                                      //if on a simulation by simulation basis, and no value specified, use the last value specified for all-sims
                                      isUpdatesEnabledForAll() );
        }
    }

    public boolean isTrackingEnabledForAll() {
        return getBooleanEnabled( ALL_SIMS, TRACKING, true );
    }

    public boolean isUpdatesEnabledForAll() {
        return getBooleanEnabled( ALL_SIMS, UPDATES, true );
    }

    public void setTrackingEnabled( String project, String sim, boolean trackingEnabled ) {
        setBoolean( project, sim, TRACKING, trackingEnabled );
    }

    public void setUpdatesEnabled( String project, String sim, boolean updatesEnabled ) {
        setBoolean( project, sim, UPDATES, updatesEnabled );
    }

    private boolean getBooleanEnabled( String project, String sim, String type, boolean defaultValue ) {
        return getBooleanEnabled( project + DOT + sim, type, defaultValue );
    }

    private boolean getBooleanEnabled( String project, String type, boolean defaultValue ) {
        return Boolean.valueOf( properties.getProperty( project + DOT + type + DOT + "enabled", "" + defaultValue ) ).booleanValue();
    }

    private void setBoolean( String project, String sim, String type, boolean value ) {
        setBooleanEnabled( project + DOT + sim, type, value );
    }

    private void setBooleanEnabled( String project, String type, boolean value ) {
        properties.setProperty( project + DOT + type + DOT + "enabled", "" + value );
        storePreferences();
    }

    public String toString() {
        return properties.toString();
    }

    public static void main( String[] args ) throws IOException {
        PhetPreferences phetPreferences = new PhetPreferences();
        System.out.println( "phetPreferences = " + phetPreferences );
    }

    public void setLastAskMeLaterTime( long time ) {
        properties.setProperty( LAST_ASK_ME_LATER_TIME, "" + time );
        storePreferences();
    }

    public long getLastAskMeLaterTime() {
        return Long.parseLong( properties.getProperty( LAST_ASK_ME_LATER_TIME, "0" ) );
    }

    public void skipThisVersion( String project, String sim, PhetVersion version ) {
        properties.setProperty( project + "." + sim + ".skip", version.getRevisionAsInt()+"" );//todo: i'd prefer to save the major.minor here, but don't have a parser handy
        storePreferences();
    }

    public int getSkip( String project, String sim ) {
        return Integer.parseInt( properties.getProperty( project + "." + sim + ".skip", "0" ) );
    }
}
