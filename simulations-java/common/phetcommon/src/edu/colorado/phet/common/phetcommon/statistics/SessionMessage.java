package edu.colorado.phet.common.phetcommon.statistics;

import java.util.Date;

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.application.SessionCounter;
import edu.colorado.phet.common.phetcommon.files.PhetInstallation;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.DeploymentScenario;
import edu.colorado.phet.common.phetcommon.util.JavaVersion.JREVersion;

/**
 * Statistics message sent when the simulation starts, indicating the start of the session.
 * This message sends lots of general information about the simulation and the user's 
 * runtime environment.
 * <p>
 * This is a singleton, because we should never have more than instance of this 
 * message per session.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class SessionMessage extends StatisticsMessage {
    
    // Versioning the messages allows us to manage data after changing message content.
    // If the content of this message is changed, you'll need to increment the version number.
    public static final String MESSAGE_VERSION = "0";
    
    private static SessionMessage instance;
    
    public static SessionMessage initInstance( ISimInfo simInfo ) {
        if ( instance != null ) {
            throw new RuntimeException( "initInstance was called more than once" );
        }
        else {
            instance = new SessionMessage( simInfo );
        }
        return instance;
    }
    
    public static SessionMessage getInstance() {
        return instance;
    }
    
    /* singleton */
    private SessionMessage( ISimInfo simInfo ) {
        super( "session", MESSAGE_VERSION );
        
        initTimeZone();
        JREVersion jre = new JREVersion();
        
        StatisticsMessageField[] fields = new StatisticsMessageField[]{
                
                // Sim data
                new StatisticsMessageField( "Project name", "sim_project", simInfo.getProjectName() ),
                new StatisticsMessageField( "Simulation name", "sim_name", simInfo.getFlavor() ),
                new StatisticsMessageField( "Simulation version (major)", "sim_major_version", simInfo.getVersion().getMajor() ),
                new StatisticsMessageField( "Simulation version (minor)", "sim_minor_version", simInfo.getVersion().getMinor() ),
                new StatisticsMessageField( "Simulation version (dev)", "sim_dev_version", simInfo.getVersion().getDev() ),
                new StatisticsMessageField( "Simulation version (revision)", "sim_svn_revision", simInfo.getVersion().getRevision() ),
                new StatisticsMessageField( "Simulation version (timestamp)", "sim_version_timestamp", simInfo.getVersion().getTimestampSeconds() ),
                new StatisticsMessageField( "Simulation version (distribution)", "sim_distribution_tag", simInfo.getDistributionTag() ),
                new StatisticsMessageField( "Language", "sim_locale_language", PhetResources.readLocale().getLanguage() ),
                new StatisticsMessageField( "Country", "sim_locale_country", PhetResources.readLocale().getCountry() ),
                new StatisticsMessageField( "Deployment type", "sim_deployment", DeploymentScenario.getInstance().toString() ),
                new StatisticsMessageField( "Is this a developer version?", "sim_dev", simInfo.isDev() + "" ),
                new StatisticsMessageField( "Total number of times this simulation has been run", "sim_total_sessions", SessionCounter.getInstance().getCount() ),
                new StatisticsMessageField( "Number of times this simulation has been run since last online", "sim_sessions_since", SessionCounter.getInstance().getCountSince() ),
                
                // Host data
                new StatisticsMessageField.SystemProperty( "Operating system name", "host_os_name", "os.name" ),
                new StatisticsMessageField.SystemProperty( "Operating system version", "host_os_version", "os.version" ),
                new StatisticsMessageField.SystemProperty( "Processor type", "host_os_arch", "os.arch" ),
                new StatisticsMessageField.SystemProperty( "Java vendor", "host_java_vendor", "java.vendor" ),
                new StatisticsMessageField( "Java version (major)", "host_java_version_major", jre.getMajorNumber() ),
                new StatisticsMessageField( "Java version (minor)", "host_java_version_minor", jre.getMinorNumber() ),
                new StatisticsMessageField( "Java version (maintenance)", "host_java_version_maintenance", jre.getMaintenanceNumber() ),
                new StatisticsMessageField.SystemProperty( "Java Web Start version", "host_java_webstart_version", "javawebstart.version" ),
                new StatisticsMessageField.SystemProperty( "Host language", "host_locale_language", "user.language" ),
                new StatisticsMessageField.SystemProperty( "Host country", "host_locale_country", "user.country" ),
                new StatisticsMessageField.SystemProperty( "Timezone", "host_java_timezone", "user.timezone" ),
                
                // User data
                new StatisticsMessageField( "Preferences file creation timestamp", "user_preference_file_creation_time", PhetPreferences.getInstance().getPreferencesFileCreationTime() ),
                new StatisticsMessageField( "Total number of times all simulations have been run", "user_total_sessions", SessionCounter.getInstance().getTotal() ),
        };
        super.addFields( fields );
        
        if ( DeploymentScenario.getInstance() == DeploymentScenario.PHET_INSTALLATION ) {
            PhetInstallation p = PhetInstallation.getInstance();
            addField( new StatisticsMessageField( "PhET installation timestamp", "user_installation_timestamp", p.getInstallationTimestamp() ) );
        }
    }

    private void initTimeZone() {
        //for some reason, user.timezone only appears if the next line is used (otherwise user.timezone is empty or null)
        new Date().toString();
    }
}
