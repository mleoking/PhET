/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.preferences.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.tracking.Trackable;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
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
public class PhetApplicationConfig implements Trackable, ITrackingInfo, ISimInfo {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final FrameSetup DEFAULT_FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // immutable
    private final String[] commandLineArgs;
    private final String flavor;
    private PhetResources resourceLoader;

    // mutable
    private FrameSetup frameSetup;
    private PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel(); // the look and feel to be initialized in launchSim

    private long simStartTimeMillis = System.currentTimeMillis();//System time is recorded on startup to facilitate tracking of multiple messages from the same sim run
    private long applicationLaunchFinishedAt;//this value is determined after launch is complete

    //----------------------------------------------------------------------------
    // Interfaces
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /*
     * Constructor with smallest number of args for the case where project & flavor names are identical.
     */

    public PhetApplicationConfig( String[] commandLineArgs, String project ) {
        this( commandLineArgs, project, project );
    }

    /*
     * Constructor with smallest number of args for a flavor.
     */
    public PhetApplicationConfig( String[] commandLineArgs, String project, String flavor ) {
        this.commandLineArgs = commandLineArgs;
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

    //----------------------------------------------------------------------------
    // Updates and Tracking stuff
    //----------------------------------------------------------------------------

    public TrackingMessage getTrackingInformation() {
        return new TrackingMessage( this, TrackingMessage.UNKNOWN_TYPE );
    }

    public String getHumanReadableTrackingInformation() {
        return getTrackingInformation().toHumanReadable();
    }

    public boolean isDev() {
        return Arrays.asList( commandLineArgs ).contains( PhetApplication.DEVELOPER_CONTROLS_COMMAND_LINE_ARG );
    }

    public long getSimStartTimeMillis() {
        return simStartTimeMillis;
    }

    public boolean isUpdatesEnabled() {
        return new UpdateApplicationManager( this ).isUpdatesEnabled();
    }

    public boolean isTrackingEnabled() {
        return new TrackingManager( this ).isTrackingCommandLineOptionSet();
    }

    public void setApplicationLaunchFinishedAt( long applicationLaunchFinishedAt ) {
        this.applicationLaunchFinishedAt = applicationLaunchFinishedAt;
    }

}
