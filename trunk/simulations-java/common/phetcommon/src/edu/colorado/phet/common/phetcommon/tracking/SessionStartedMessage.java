package edu.colorado.phet.common.phetcommon.tracking;

import java.awt.Toolkit;
import java.util.Date;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * Tracking message sent when the simulation starts, indicating the start of the session.
 * This message sends lots of general information about the simulation and the user's 
 * runtime environment.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class SessionStartedMessage extends TrackingMessage {
    
    // Versioning the messages allows us to manage data after changing message content.
    // If the content of this message is changed, you'll need to increment the version number.
    public static final String MESSAGE_VERSION = "1";
    
    public SessionStartedMessage( PhetApplicationConfig config ) {
        super( new SessionID( config ), "session_started", MESSAGE_VERSION );
        initTimeZone();
        
        TrackingMessageField[] fields = new TrackingMessageField[]{
                
                // Common framework info
                new TrackingMessageField( "user_first_seen_time", PhetPreferences.getInstance().getPreferencesFileCreatedAtMillis() + "" ),
                new TrackingMessageField( "sim_started_at_time", config.getSimStartTimeMillis() + "" ),
                new TrackingMessageField( "sim_startup_time", config.getElapsedStartupTime() + "" ),
                
                // Sim info
                new TrackingMessageField( "project", config.getProjectName() ),
                new TrackingMessageField( "sim_name", config.getFlavor() ),
                new TrackingMessageField( "session_count", toString( config.getSessionCount() ) ),
                new TrackingMessageField( "sim_version_debug", config.getVersion().formatMajorMinorDev() ), // debug, human readable
                new TrackingMessageField( "sim_major_version", config.getVersion().getMajor() ),
                new TrackingMessageField( "sim_minor_version", config.getVersion().getMinor() ),
                new TrackingMessageField( "sim_dev_version", config.getVersion().getDev() ),
                new TrackingMessageField( "sim_svn_revision", config.getVersion().getRevision() ),
                new TrackingMessageField( "sim_locale_language", PhetResources.readLocale().getLanguage() ),
                new TrackingMessageField( "sim_locale_country", PhetResources.readLocale().getCountry() ),
                new TrackingMessageField( "is_dev", config.isDev() + "" ),
                new TrackingMessageField( "is_running_from_phet_installation", Boolean.toString( PhetUtilities.isPhetInstallation() ) ),
                new TrackingMessageField( "is_running_from_standalone_jar", Boolean.toString( PhetUtilities.isRunningFromStandaloneJar() ) ),
                new TrackingMessageField( "is_running_from_website", Boolean.toString( PhetUtilities.isRunningFromWebsite() ) ),

                // Host info
                new TrackingMessageField.SystemProperty( "os_name", "os.name" ),
                new TrackingMessageField.SystemProperty( "os_version", "os.version" ),
                new TrackingMessageField.SystemProperty( "os_arch", "os.arch" ),
                new TrackingMessageField.SystemProperty( "java_webstart_version", "javawebstart.version" ),
                new TrackingMessageField.SystemProperty( "java_version", "java.version" ),
                new TrackingMessageField.SystemProperty( "java_vendor", "java.vendor" ),
                new TrackingMessageField.SystemProperty( "host_locale_language", "user.language" ),
                new TrackingMessageField.SystemProperty( "host_locale_country", "user.country" ),
                new TrackingMessageField.SystemProperty( "timezone", "user.timezone" ),
                new TrackingMessageField( "screen_width", Toolkit.getDefaultToolkit().getScreenSize().width + "" ),
                new TrackingMessageField( "screen_height", Toolkit.getDefaultToolkit().getScreenSize().height + "" ),
        };
        super.addFields( fields );
    }

    private void initTimeZone() {
        //for some reason, user.timezone only appears if the next line is used (otherwise user.timezone is empty or null)
        new Date().toString();
    }
    
    private String toString( Integer i ) {
        return ( i == null ) ? "null" : i.toString();
    }
}
