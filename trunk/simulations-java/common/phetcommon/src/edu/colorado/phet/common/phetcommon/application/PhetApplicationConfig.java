/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.util.Arrays;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.statistics.IStatistics;
import edu.colorado.phet.common.phetcommon.statistics.SessionMessage;
import edu.colorado.phet.common.phetcommon.util.DeploymentScenario;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

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
public class PhetApplicationConfig implements IStatistics, ISimInfo {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final FrameSetup DEFAULT_FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );
    public static final String DEFAULT_DISTRIBUTION_ID = "general";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // immutable
    private final String[] commandLineArgs;
    private final String flavor;
    private final PhetResources resourceLoader;
    private final SessionCounter sessionCounter;

    // mutable
    private FrameSetup frameSetup;
    private PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel(); // the look and feel to be initialized in launchSim

    private final long simStartTimeMillis = System.currentTimeMillis();//System time is recorded on startup to facilitate tracking of multiple messages from the same sim run
    private long applicationLaunchFinishedAt;//this value is determined after launch is complete

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor where project & flavor names are identical.
     * @param commandLineArgs
     * @param project
     */
    public PhetApplicationConfig( String[] commandLineArgs, String project ) {
        this( commandLineArgs, project, project );
    }

    /**
     * Constructor where project & flavor names are different.
     * @param commandLineArgs
     * @param project
     * @param flavor
     */
    public PhetApplicationConfig( String[] commandLineArgs, String project, String flavor ) {
        this.commandLineArgs = commandLineArgs;
        this.flavor = flavor;
        this.resourceLoader = new PhetResources( project );
        this.frameSetup = DEFAULT_FRAME_SETUP;
        this.phetLookAndFeel = new PhetLookAndFeel();
        this.sessionCounter = SessionCounter.initInstance( project, flavor );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public String[] getCommandLineArgs() {
        return commandLineArgs;
    }

    public boolean hasCommandLineArg( String arg ) {
        return Arrays.asList( commandLineArgs ).contains( arg );
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
    
    /**
     * Gets the number of times that the simulation has been run,
     * including the current invocation.
     * This will be zero if the sim is running in an environment 
     * where access to the local file system is denied.
     * 
     * @return int, possibly zero
     */
    public int getSessionCount() {
        return sessionCounter.getCount();
    }
    
    /**
     * Gets the total number of times that all simulations have been run,
     * including the current invocation.
     * This will be zero if the sim is running in an environment 
     * where access to the local file system is denied.
     * 
     * @return int, possibly zero
     */
    public int getSessionCountTotal() {
        return sessionCounter.getTotal();
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
    
    public String getDistributionId() {
        //TODO #1086, read the distribution id from an optional file stored in the JAR.
        return DEFAULT_DISTRIBUTION_ID;
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

    /**
     * Gets the localized simulation description.
     *
     * @return description
     */
    public String getDescription() {
        return resourceLoader.getDescription( flavor );
    }

    public String getVersionForTitleBar() {
        return getVersion().formatForTitleBar();
    }

    /**
     * Gets the simulation credits.
     *
     * @return credits, possibly null
     */
    public String getCredits() {
        return resourceLoader.getCredits();
    }

    /**
     * Retrieves the object that encapsulates the project's version information.
     *
     * @return PhetProjectVersion
     */
    public PhetVersion getVersion() {
        return resourceLoader.getVersion();
    }

    public long getApplicationLaunchFinishedAt() {
        return applicationLaunchFinishedAt;
    }

    public long getElapsedStartupTime() {
        return getApplicationLaunchFinishedAt() - getSimStartTimeMillis();
    }

    public String getHumanReadableStatistics() {
        return new SessionMessage( this ).toHumanReadable();
    }

    public boolean isDev() {
        return Arrays.asList( commandLineArgs ).contains( PhetApplication.DEVELOPER_CONTROLS_COMMAND_LINE_ARG );
    }

    public long getSimStartTimeMillis() {
        return simStartTimeMillis;
    }

    public Locale getLocale() {
        return PhetResources.readLocale();
    }
    
    /**
     * Should the updates feature be included at runtime?
     * @return
     */
    public boolean isUpdatesFeatureIncluded() {
        return hasCommandLineArg( "-updates" ) && !DeploymentScenario.isWebsite();
    }
    
    /**
     * Should the tracking feature be included at runtime?
     * @return
     */
    public boolean isStatisticsFeatureIncluded() {
        return hasCommandLineArg( "-tracking" ) && !DeploymentScenario.isWebsite();
    }

    public boolean isUpdatesEnabled() {
        return isUpdatesFeatureIncluded() && PhetPreferences.getInstance().isUpdatesEnabled();
    }
    
    public boolean isStatisticsEnabled() {
        return isStatisticsFeatureIncluded() && PhetPreferences.getInstance().isStatisticsEnabled();
    }

    public void setApplicationLaunchFinishedAt( long applicationLaunchFinishedAt ) {
        this.applicationLaunchFinishedAt = applicationLaunchFinishedAt;
    }

}
