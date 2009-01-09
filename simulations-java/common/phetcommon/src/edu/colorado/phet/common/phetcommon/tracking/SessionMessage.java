package edu.colorado.phet.common.phetcommon.tracking;

import java.util.Date;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.JavaVersion.JREVersion;

/**
 * Tracking message sent when the simulation starts, indicating the start of the session.
 * This message sends lots of general information about the simulation and the user's 
 * runtime environment.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class SessionMessage extends TrackingMessage {
    
    // Versioning the messages allows us to manage data after changing message content.
    // If the content of this message is changed, you'll need to increment the version number.
    public static final String MESSAGE_VERSION = "1";
    
    public SessionMessage( PhetApplicationConfig config ) {
        super( "session", MESSAGE_VERSION );
        initTimeZone();
        JREVersion jre = new JREVersion();
        TrackingMessageField[] fields = new TrackingMessageField[]{
                
                // User data
                new TrackingMessageField( "user_preference_file_creation_time", PhetPreferences.getInstance().getPreferencesFileCreatedAtMillis() ),
                new TrackingMessageField( "user_total_sessions", config.getSessionCountTotal() ),
                
                // Sim data
                new TrackingMessageField( "sim_project", config.getProjectName() ),
                new TrackingMessageField( "sim_name", config.getFlavor() ),
                new TrackingMessageField( "sim_sessions", config.getSessionCount() ),
                new TrackingMessageField( "sim_major_version", config.getVersion().getMajor() ),
                new TrackingMessageField( "sim_minor_version", config.getVersion().getMinor() ),
                new TrackingMessageField( "sim_dev_version", config.getVersion().getDev() ),
                new TrackingMessageField( "sim_svn_revision", config.getVersion().getRevision() ),
                new TrackingMessageField( "sim_distribution_id", config.getDistributionId() ),
                new TrackingMessageField( "sim_locale_language", PhetResources.readLocale().getLanguage() ),
                new TrackingMessageField( "sim_locale_country", PhetResources.readLocale().getCountry() ),
                new TrackingMessageField( "sim_scenario", config.getRuntimeScenario() ),
                new TrackingMessageField( "sim_dev", config.isDev() + "" ),
                
                // Host data
                new TrackingMessageField.SystemProperty( "host_os_name", "os.name" ),
                new TrackingMessageField.SystemProperty( "host_os_version", "os.version" ),
                new TrackingMessageField.SystemProperty( "host_os_arch", "os.arch" ),
                new TrackingMessageField.SystemProperty( "host_java_vendor", "java.vendor" ),
                new TrackingMessageField( "host_java_version_major", jre.getMajorNumber() ),
                new TrackingMessageField( "host_java_version_minor", jre.getMinorNumber() ),
                new TrackingMessageField( "host_java_version_maintenance", jre.getMaintenanceNumber() ),
                new TrackingMessageField( "host_java_version_update", jre.getUpdateNumber() ),
                new TrackingMessageField.SystemProperty( "host_java_webstart_version", "javawebstart.version" ),
                new TrackingMessageField.SystemProperty( "host_locale_language", "user.language" ),
                new TrackingMessageField.SystemProperty( "host_locale_country", "user.country" ),
                new TrackingMessageField.SystemProperty( "host_timezone", "user.timezone" ),
                
                // Debug field for this that are split into multiple fields
                new TrackingMessageField( "debug_sim_version", config.getVersion().formatMajorMinorDevRevision() ),
                new TrackingMessageField( "debug_host_java_version", jre.getVersion() ),
        };
        super.addFields( fields );
    }

    private void initTimeZone() {
        //for some reason, user.timezone only appears if the next line is used (otherwise user.timezone is empty or null)
        new Date().toString();
    }
}
