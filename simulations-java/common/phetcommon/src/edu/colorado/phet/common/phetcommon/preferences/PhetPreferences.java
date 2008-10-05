package edu.colorado.phet.common.phetcommon.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * PhetPreferences is responsible for storing persistent user preference information, across simulations.
 */

//todo: only create preferences file when user changes from default?
//todo: sync file on frame activitation?
public class PhetPreferences {
    private Properties properties = new Properties();
    private static final String UPDATE_PREFERENCE_KEY = "updates";
    private static final String TRACKING_PREFERENCE_KEY = "tracking";
    private static PhetPreferences preferences;

    private PhetPreferences() {
    }

    public static PhetPreferences getPreferences() {
        if ( preferences == null ) {
            preferences = new PhetPreferences();
            preferences.load();
        }
        return preferences;
    }

    public UpdatePreference getUpdatePreference() {
        return UpdatePreference.get( getValue( UPDATE_PREFERENCE_KEY, UpdatePreference.TYPES[0].getPropertyValue() ) );
    }

    private String getValue( String key, String defaultValue ) {
        return properties.getProperty( key, defaultValue );
    }

    public TrackingPreference getTrackingPreferences() {
        return TrackingPreference.get( getValue( TRACKING_PREFERENCE_KEY, TrackingPreference.TYPES[0].getPropertyValue() ) );
    }

    public void setUpdatePreference( UpdatePreference updatePreferences ) {
        if ( updatePreferences != getUpdatePreference() ) {
            properties.setProperty( UPDATE_PREFERENCE_KEY, updatePreferences.getPropertyValue() );
            storePreferences();
            notifyUpdatePreferencesChanged();
        }
    }

    //todo: implement these
    private void notifyTrackingPreferencesChanged() {
    }

    private void notifyUpdatePreferencesChanged() {
    }

    private void load() {
        try {
            properties.load( new FileInputStream( getPreferencesFile() ) );
        }
        catch( IOException e ) {//todo: error handling
//            e.printStackTrace();
            properties = new Properties();
            properties.setProperty( UPDATE_PREFERENCE_KEY, UpdatePreference.TYPES[0].getPropertyValue() );
            properties.setProperty( TRACKING_PREFERENCE_KEY, TrackingPreference.TYPES[0].getPropertyValue() );
        }
    }

    private void storePreferences() {
        try {
            properties.store( new FileOutputStream( getPreferencesFile() ), "Preferences for PhET, see http://phet.colorado.edu" );
        }
        catch( IOException e ) {//todo: error handling
            e.printStackTrace();
        }
    }

    private File getPreferencesFile() {
        return new File( getPhetDir(), "phet-preferences.properties" );
    }

    private File getPhetDir() {
        File phetDir = new File( getUserHomeDir(), ".phet" );
        if ( !phetDir.exists() ) {
            phetDir.mkdirs();//todo: error handling
        }
        return phetDir;
    }

    private File getUserHomeDir() {
        return new File( System.getProperty( "user.home" ) );
    }

    public void setTrackingPreferences( TrackingPreference trackingPreferences ) {
        if ( trackingPreferences != getTrackingPreferences() ) {
            properties.setProperty( TRACKING_PREFERENCE_KEY, trackingPreferences.getPropertyValue() );
            storePreferences();
            notifyTrackingPreferencesChanged();
        }
    }


    public String toString() {
        return properties.toString();
    }

    public static void main( String[] args ) {
        PhetPreferences p = getPreferences();
        System.out.println( "p=" + p );
        p.storePreferences();
    }
}
