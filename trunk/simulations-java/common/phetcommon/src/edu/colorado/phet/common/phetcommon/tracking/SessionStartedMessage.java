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
        TrackingMessageField[] entriesArray = new TrackingMessageField[]{
                new TrackingMessageField( "tracker-version", TRACKER_VERSION ),
                new TrackingMessageField( "message-version", MESSAGE_VERSION ),

                //Sim info first
                new TrackingMessageField( "project", config.getProjectName() ),
                new TrackingMessageField( "sim", config.getFlavor() ),
                new TrackingMessageField( "sim-type", "java" ), // to easily distinguish between Java and Flash sims
                new TrackingMessageField( "sim-version", config.getVersion().formatMajorMinorDev() ),
                new TrackingMessageField( "svn-revision", config.getVersion().getRevision() ),
                new TrackingMessageField( "sim-locale", PhetResources.readLocale().toString() ),
                new TrackingMessageField( "dev", config.isDev() + "" ),

                //Then general to specific information about machine config
                new TrackingMessageField.SystemProperty( "os.name" ),
                new TrackingMessageField.SystemProperty( "os.version" ),
                new TrackingMessageField.SystemProperty( "os.arch" ),

                new TrackingMessageField.SystemProperty( "javawebstart.version" ),
                new TrackingMessageField.SystemProperty( "java.version" ),
                new TrackingMessageField.SystemProperty( "java.vendor" ),

                new TrackingMessageField.SystemProperty( "user.country" ),
                new TrackingMessageField.SystemProperty( "user.timezone" ),
                new TrackingMessageField( "locale-default", Locale.getDefault().toString() ),
                new TrackingMessageField( PhetPreferences.KEY_PREFERENCES_FILE_CREATION_TIME, PhetPreferences.getInstance().getPreferencesFileCreatedAtMillis() + "" ),
                new TrackingMessageField( "sim-started-at", config.getSimStartTimeMillis() + "" ),
                new TrackingMessageField( "sim-startup-time", config.getElapsedStartupTime() + "" ),

                new TrackingMessageField( "time", new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss" ).format( new Date() ) )
        };
        super.addFields( entriesArray );
    }

    private void initTimeZone() {
        //for some reason, user.timezone only appears if the next line is used (otherwise user.timezone is empty or null)
        new Date().toString();
    }
}
