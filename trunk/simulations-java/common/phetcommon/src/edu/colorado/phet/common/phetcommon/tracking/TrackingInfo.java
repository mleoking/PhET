package edu.colorado.phet.common.phetcommon.tracking;

import java.util.Date;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class TrackingInfo {
    private TrackingEntry[] entries;

    public TrackingInfo( PhetApplicationConfig config ) {
        initTimeZone();
        entries = new TrackingEntry[]{
                new TrackingEntry( "type", "sim-launched" ),

                //Sim info first
                new TrackingEntry( "project", config.getProjectName() ),
                new TrackingEntry( "sim", config.getFlavor() ),
                new TrackingEntry( "sim-version", config.getVersion().toString() ),
                new TrackingEntry( "sim-locale", PhetResources.readLocale().toString() ),
                new TrackingEntry( "dev", config.isDev() + "" ),

                //Then general to specific information about machine config
                new TrackingEntry.SystemProperty( "os.name" ),
                new TrackingEntry.SystemProperty( "os.version" ),
                new TrackingEntry.SystemProperty( "os.arch" ),

                new TrackingEntry.SystemProperty( "javawebstart.version" ),
                new TrackingEntry.SystemProperty( "java.version" ),
                new TrackingEntry.SystemProperty( "java.vendor" ),

                new TrackingEntry.SystemProperty( "user.country" ),
                new TrackingEntry.SystemProperty( "user.timezone" ),
                new TrackingEntry( "locale-default", Locale.getDefault().toString() ),
                new TrackingEntry( PhetPreferences.KEY_PREFERENCES_FILE_CREATION_TIME, PhetPreferences.getInstance().getPreferencesFileCreatedAtMillis() + "" ),

//                new TrackingEntry( "time", new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss" ).format( new Date() ) )
        };
    }

    private void initTimeZone() {
        //for some reason, user.timezone only appears if the next line is used (otherwise user.timezone is empty or null)
        new Date().toString();
    }

    public String toPHP() {
        String php = "";
        for ( int i = 0; i < entries.length; i++ ) {
            if ( i > 0 ) {
                php += "&";
            }
            php += entries[i].toPHP();
        }
        return php;
    }

    public String toHumanReadable() {
        String text = "";
        for ( int i = 0; i < entries.length; i++ ) {
            if ( i > 0 ) {
                text += "\n";
            }
            text += entries[i].toHumanReadable();
        }
        return text;
    }
}
