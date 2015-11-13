// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.application;

import java.util.Arrays;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.util.DeploymentScenario;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.StringUtil;

/**
 * PhetApplicationConfig encapsulates the information required to configure
 * a PhetApplication, including transparent access to the project's
 * properties file. It is also responsible for launching an application.
 * <p/>
 * Some terminology:
 * <ul>
 * <li>A project is a directory name in the PhET source code repository.
 * <li>More than one simulation may live under a project directory, be built
 * from the project's source code, and use the project's resources.
 * Each of these simulations is referred to as a flavor.
 * <li>If a flavor name is not specified, it defaults to the project name.
 * </ul>
 *
 * @author John De Goes / Chris Malley
 */
public class PhetApplicationConfig implements ISimInfo {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    //This frame setup should be used for all phet applications, to ensure a consistent window size
    public static final FrameSetup.CenteredWithSize DEFAULT_FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // immutable
    private final String[] commandLineArgs;
    private final String flavor;
    private final PhetResources resourceLoader;

    // mutable
    private FrameSetup frameSetup;
    private PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel(); // the look and feel to be initialized in launchSim

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor where project & flavor names are identical.
     *
     * @param commandLineArgs
     * @param project
     */
    public PhetApplicationConfig( String[] commandLineArgs, String project ) {
        this( commandLineArgs, project, project );
    }

    /**
     * Constructor where project & flavor names are different.
     *
     * @param commandLineArgs
     * @param project
     * @param flavor
     */
    public PhetApplicationConfig( String[] commandLineArgs, String project, String flavor ) {
        this.commandLineArgs = commandLineArgs;
        if ( hasCommandLineArg( "-log" ) ) {
            LoggingUtils.enableAllLogging( "edu.colorado.phet.common.phetcommon" );
        }
        this.flavor = flavor;
        this.resourceLoader = new PhetResources( project );
        this.frameSetup = DEFAULT_FRAME_SETUP;
        this.phetLookAndFeel = new PhetLookAndFeel();
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public String[] getCommandLineArgs() {
        return commandLineArgs;
    }

    public boolean hasCommandLineArg( String arg ) {
        return StringUtil.contains( commandLineArgs, arg );
    }

    // Gets the argument that follows a command line option. Eg, for "-foo bar", return "bar".
    public String getOptionArg( String option ) {
        int s = Arrays.asList( commandLineArgs ).indexOf( option );
        if ( s >= 0 && s <= commandLineArgs.length - 2 && !commandLineArgs[s + 1].startsWith( "-" ) ) {
            return commandLineArgs[s + 1];
        }
        else {
            return null;
        }
    }

    public void setFrameSetup( FrameSetup frameSetup ) {
        this.frameSetup = frameSetup;
    }

    public FrameSetup getFrameSetup() {
        return frameSetup;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setLookAndFeel( PhetLookAndFeel phetLookAndFeel ) {
        this.phetLookAndFeel = phetLookAndFeel;
    }

    public PhetLookAndFeel getLookAndFeel() {
        return phetLookAndFeel;
    }

    public PhetResources getResourceLoader() {
        return resourceLoader;
    }

    public String getProjectName() {
        return resourceLoader.getProjectName();
    }

    public boolean isPreferencesEnabled() {
        return isStatisticsFeatureIncluded() || isUpdatesFeatureIncluded();
    }

    /**
     * Returns the distribution identifier associated with the sim's JAR file.
     * This is used to identify specific distributions of a sim, for example
     * as bundled with a textbook.
     *
     * @return
     */

    public String getDistributionTag() {
        return resourceLoader.getDistributionTag();
    }

    //----------------------------------------------------------------------------
    // Standard properties
    //----------------------------------------------------------------------------

    /**
     * Gets the localized simulation name.
     *
     * @return name
     */
    public String getName() {
        return resourceLoader.getName( flavor );
    }

    public String getVersionForTitleBar() {
        return getVersion().formatForTitleBar();
    }

    /**
     * Retrieves the object that encapsulates the project's version information.
     *
     * @return PhetProjectVersion
     */
    public PhetVersion getVersion() {
        return resourceLoader.getVersion();
    }

    public boolean isDev() {
        return hasCommandLineArg( PhetApplication.DEVELOPER_CONTROLS_COMMAND_LINE_ARG );
    }

    public Locale getLocale() {
        return PhetResources.readLocale();
    }

    /**
     * Should the updates feature be included at runtime?
     *
     * @return
     */
    public boolean isUpdatesFeatureIncluded() {
        return ( !hasCommandLineArg( "-updates-off" ) ) && DeploymentScenario.getInstance().isUpdatesEnabled();
    }

    /**
     * Should the statistics feature be included at runtime?
     *
     * @return
     */
    public boolean isStatisticsFeatureIncluded() {
        return ( !hasCommandLineArg( "-statistics-off" ) ) && DeploymentScenario.getInstance().isStatisticsEnabled();
    }

    public boolean isUpdatesEnabled() {
        return isUpdatesFeatureIncluded() && PhetPreferences.getInstance().isUpdatesEnabled();
    }

    public boolean isStatisticsEnabled() {
        return isStatisticsFeatureIncluded() && PhetPreferences.getInstance().isStatisticsEnabled();
    }

    // Provided so that customer installers can disable the sponsor feature via JNLP.
    public boolean isSponsorFeatureEnabled() {
        return !hasCommandLineArg( "-sponsor-off" );
    }

    public boolean isAskFeatureEnabled() {
        return hasCommandLineArg( "-ask" );
    }

    /**
     * Project JAR file is named <project>_all.jar
     */
    public static String getProjectJarName( String project ) {
        return project + "_all.jar";
    }
}
