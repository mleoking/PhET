package edu.colorado.phet.common.phetcommon.tracking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class SessionStartedMessage extends TrackingMessage {
    public SessionStartedMessage( PhetApplicationConfig config ) {
        super( new SessionID( config ), "session-started" );
        initTimeZone();
        TrackingEntry[] entriesArray = new TrackingEntry[]{
                new TrackingEntry( "tracker-version", TRACKER_VERSION ),
                new TrackingEntry( "message-version", MESSAGE_VERSION ),

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
                new TrackingEntry( "sim-started-at", config.getSimStartTimeMillis() + "" ),
                new TrackingEntry( "sim-startup-time", config.getElapsedStartupTime() + "" ),

                new TrackingEntry( "time", new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss" ).format( new Date() ) )
        };
        super.addAllEntries( entriesArray );
    }

    private void initTimeZone() {
        //for some reason, user.timezone only appears if the next line is used (otherwise user.timezone is empty or null)
        new Date().toString();
    }
}
