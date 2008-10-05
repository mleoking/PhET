/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.CommandLineUtils;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.JTabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * The base class for PhET applications.
 * <p/>
 * The prefered method of creating and starting an application is shown here:
 * <code>
 * <br>
 * PhetApplication myApp = new PhetApplication( args, "Title", "Description", "Version, frameSetup );<br>
 * myApp.addModule(module1);<br>
 * myApp.addModule(module2);<br>
 * myApp.addModule(module3);<br>
 * myApp.startApplication();<br>
 * </code>
 * <p/>
 */
public class PhetApplication {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    /** Command line argument to enable developer-only features. */
    public static final String DEVELOPER_CONTROLS_COMMAND_LINE_ARG = "-dev";
    
    /**
     * Mechanism for determining which graphics subsystem we're using
     */
    private static final String DEBUG_MENU_COMMAND_LINE_ARG = "-d";
    private static PhetApplication latestInstance = null;
    private static ArrayList phetApplications = new ArrayList();
    
    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------
    
    private TabbedPaneType tabbedPaneType;
    private volatile PhetApplicationConfig applicationConfig;
    private final boolean developerControlsEnabled;

    private String title;
    private String description;
    private String version;
    private PhetFrame phetFrame;
    private ModuleManager moduleManager;
    private AWTSplashWindow splashWindow;
    private Frame splashWindowOwner;
    private PhetAboutDialog aboutDialog; // not null only when About dialog is visible
    public static int instances=0;

    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------
    
    protected PhetApplication( PhetApplicationConfig config ) {
        this( config.getCommandLineArgs(), config.getName(), config.getDescription(), config.getVersion().formatForTitleBar(), config.getFrameSetup() ,JTABBED_PANE_TYPE,config);
    }

    protected PhetApplication( PhetApplicationConfig config, TabbedPaneType tabbedPaneType ) {
        this( config.getCommandLineArgs(), config.getName(), config.getDescription(), config.getVersion().formatForTitleBar(), config.getFrameSetup(), tabbedPaneType ,config );
    }

    /**
     * Initialize a PhetApplication with a default FrameSetup.
     *
     * @param args        Command line args
     * @param title       Title that appears in the frame and the About dialog
     * @param description Appears in the About dialog
     * @param version     Appears in the About dialog
     * @deprecated
     */
    public PhetApplication( String[] args, String title, String description, String version) {
        this( args, title, description, version, new FrameSetup.CenteredWithSize( getScreenSize().width, getScreenSize().height - 150 ) );
    }

    /**
     * Constructor
     *
     * @param args        Command line args
     * @param title       Title that appears in the frame and the About dialog
     * @param description Appears in the About dialog
     * @param version     Appears in the About dialog
     * @param frameSetup  Defines the size and location of the frame
     * @deprecated
     */
    public PhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup ) {
        this( args, title, description, version, frameSetup, JTABBED_PANE_TYPE );
    }

    /**
     * @deprecated
     */
    public PhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup, TabbedPaneType tabbedPaneType ) {
        this( args, title, description, version, frameSetup, tabbedPaneType, null );
    }
    /**
     * @deprecated
     */
    public PhetApplication( String[] args, String title, String description, String version, FrameSetup frameSetup, TabbedPaneType tabbedPaneType ,PhetApplicationConfig config) {
        this.applicationConfig=config;
        this.developerControlsEnabled = CommandLineUtils.contains( args, DEVELOPER_CONTROLS_COMMAND_LINE_ARG );
                
        // Put up a dialog that lets the user know that the simulation is starting up
        showSplashWindow( title );
        this.tabbedPaneType = tabbedPaneType;
        latestInstance = this;
        phetApplications.add( this );

        this.title = title;
        this.description = description;
        this.version = version;

        this.moduleManager = new ModuleManager( this );
        phetFrame = createPhetFrame();
        frameSetup.initialize( phetFrame );

        // Handle command line arguments
        parseArgs( args );
        instances++;
    }

    //----------------------------------------------------------------
    // 
    //----------------------------------------------------------------
    
    /**
     * Are developer controls enabled?
     * 
     * @return true or false
     */
    public boolean isDeveloperControlsEnabled() {
        return developerControlsEnabled;
    }
    
    public PhetApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    /**
     * Get the last created PhetApplication.
     *
     * @return last created PhetApplication.
     */
    public static PhetApplication instance() {
        return latestInstance;
    }

    /**
     * Get all created PhetApplications.
     *
     * @return all created PhetApplications.
     */
    public static PhetApplication[] instances() {
        return (PhetApplication[]) phetApplications.toArray( new PhetApplication[0] );
    }
    
    /**
     * Creates the PhetFrame for the application
     * <p/>
     * Concrete subclasses implement this
     *
     * @return The PhetFrame
     */
    protected PhetFrame createPhetFrame() {
        return new PhetFrame( this );
    }

    private void showSplashWindow( String title ) {
        if ( splashWindow == null ) {
            // PhetFrame doesn't exist when this is called, so create and manage the window's owner.
            splashWindowOwner = new Frame();
            splashWindow = new AWTSplashWindow( splashWindowOwner, title );
            splashWindow.show();
        }
    }

    public AWTSplashWindow getSplashWindow() {
        return splashWindow;
    }

    private void disposeSplashWindow() {
        if ( splashWindow != null ) {
            splashWindow.dispose();
            splashWindow = null;
            // Clean up the window's owner that we created in showSplashWindow.
            splashWindowOwner.dispose();
            splashWindowOwner = null;
        }
    }

    private static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * Processes command line arguments. May be extended by subclasses.
     *
     * @param args
     */
    protected void parseArgs( String[] args ) {
        for ( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if ( arg.equals( DEBUG_MENU_COMMAND_LINE_ARG ) ) {
//                phetFrame.addDebugMenu();
                //todo generalize debug menu
            }
        }
    }

    /**
     * Starts the PhetApplication.
     * <p/>
     * Sets up the mechanism that sets the reference sizes of all ApparatusPanel2 instances.
     */
    public void startApplication() {
        if ( moduleManager.numModules() == 0 ) {
            throw new RuntimeException( "No modules in module manager" );
        }

        // Set up a mechanism that will set the reference sizes of all ApparatusPanel2 instances
        // after the PhetFrame has been set to its startup size.
        // When the outer WindowAdapter gets called, the PhetFrame is
        // at the proper size, but the ApparatusPanel2 has not yet gotten its resize event.
        phetFrame.addWindowFocusListener( new WindowAdapter() {
            public void windowGainedFocus( WindowEvent e ) {
                disposeSplashWindow();
                initializeModuleReferenceSizes();
                phetFrame.removeWindowFocusListener( this );
            }
        } );

        moduleManager.setActiveModule( getStartModule() );
        phetFrame.setVisible( true );


        updateLogoVisibility();
//        started = true;
    }

    /**
     * This is supposed hides the logo panel in the control panel if there is already a logo visible in the tabbed module pane,
     * so that both aren't visible by default..
     * <p/>
     * This method appears to give the correct behavior in the test program: TestPiccoloPhetApplication
     * <p/>
     * This method and functionality will be removed if is too awkward in practice (for example, too confusing to override this default).
     */
    protected void updateLogoVisibility() {
        for ( int i = 0; i < moduleManager.numModules(); i++ ) {
            if ( moduleAt( i ).isLogoPanelVisible() && phetFrame.getTabbedModulePane() != null && phetFrame.getTabbedModulePane().getLogoVisible() ) {
                moduleAt( i ).setLogoPanelVisible( false );
            }
        }
    }

    private void initializeModuleReferenceSizes() {
        for ( int i = 0; i < moduleManager.numModules(); i++ ) {
            Module module = moduleManager.moduleAt( i );
            module.setReferenceSize();
        }
    }

    /**
     * Get the PhetFrame for this Application.
     *
     * @return the PhetFrame for this Application.
     */
    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    //----------------------------------------------------------------
    // Module-related methods
    //----------------------------------------------------------------

    /**
     * Creates the tabbed pane for the modules in the application.
     *
     * @return a tabbed module pane
     */
    public ITabbedModulePane createTabbedPane() {
        return tabbedPaneType.createTabbedPane();
    }

    /**
     * Sets the modules in the application
     *
     * @param modules
     */
    public void setModules( Module[] modules ) {
        moduleManager.setModules( modules );
    }

    /**
     * Remove a Module from this PhetApplication.
     *
     * @param module the Module to remove.
     */
    public void removeModule( Module module ) {
        moduleManager.removeModule( module );
    }

    /**
     * Add one Module to this PhetApplication.
     *
     * @param module the Module to add.
     */
    public void addModule( Module module ) {
        moduleManager.addModule( module );
    }

    /**
     * Get the specified Module.
     *
     * @param i the Module index
     * @return the Module.
     */
    public Module moduleAt( int i ) {
        return moduleManager.moduleAt( i );
    }

    /**
     * Gets a module based on its index.
     * (This is a more common name for the moduleAt method.)
     *
     * @param i
     * @return the module
     */
    public Module getModule( int i ) {
        return moduleAt( i );
    }

    /**
     * Set the specified Module to be active.
     *
     * @param module the module to activate.
     */
    public void setActiveModule( Module module ) {
        moduleManager.setActiveModule( module );
    }

    /**
     * Set the specified Module to be active.
     *
     * @param i the module index to activate.
     */
    public void setActiveModule( int i ) {
        moduleManager.setActiveModule( i );
    }
    
    /**
     * Gets the module that will be activated on startup.
     * By default, this is the first module added.
     * To change the default, call setStartupModule.
     * 
     * @return Module
     */
    public Module getStartModule() {
        return moduleManager.getStartModule();
    }
    
    /**
     * Sets the module that will be activated on startup.
     * 
     * @param module
     */
    public void setStartModule( Module module ) {
        moduleManager.setStartModule( module );
    }

    /**
     * Add a ModuleObserver to this PhetApplication to observe changes in the list of Modules, and which Module is active.
     *
     * @param moduleObserver
     */
    public void addModuleObserver( ModuleObserver moduleObserver ) {
        moduleManager.addModuleObserver( moduleObserver );
    }

    /**
     * Get the index of the specified Module.
     *
     * @param m
     * @return the index of the specified Module.
     */
    public int indexOf( Module m ) {
        return moduleManager.indexOf( m );
    }

    /**
     * Get the number of modules.
     *
     * @return the number of modules.
     */
    public int numModules() {
        return moduleManager.numModules();
    }

    /**
     * Returns the active Module, or null if no module has been activated yet.
     *
     * @return the active Module, or null if no module has been activated yet.
     */
    public Module getActiveModule() {
        return moduleManager.getActiveModule();
    }

    /**
     * Get the title for this PhetApplication.
     *
     * @return the title.
     * @deprecated Use getProjectConfig()
     */
    public String getTitle() {
        if ( getApplicationConfig() != null ) {
            return getApplicationConfig().getName();
        }
        return title;
    }

    /**
     * Get the description for this PhetApplication.
     *
     * @return the description.
     * @deprecated Use getProjectConfig()
     */
    public String getDescription() {
        if ( getApplicationConfig() != null ) {
            return getApplicationConfig().getDescription();
        }
        return description;
    }

    /**
     * Get the version string for this PhetApplication.
     *
     * @return the version string.
     * @deprecated Use getProjectConfig()
     */
    public String getVersion() {
        if ( getApplicationConfig() != null ) {
            return getApplicationConfig().getVersion().formatForTitleBar();
        }
        return version;
    }

    /**
     * Gets the credits for the simulations.
     *
     * @return
     * @deprecated use getProjectConfig
     */
    public String getCredits() {
        String credits = null;
        if ( getApplicationConfig() != null ) {
            credits = getApplicationConfig().getCredits();
        }
        return credits;
    }

    /**
     * Adds modules.
     *
     * @param m the array of modules to add
     */
    public void addModules( Module[] m ) {
        for ( int i = 0; i < m.length; i++ ) {
            Module module = m[i];
            addModule( module );
        }
    }

    /**
     * Pauses the PhetApplication (including any Modules that are active).
     */
    public void pause() {
        getActiveModule().deactivate();
    }

    /**
     * Resumes progress of the PhetApplication (including any Modules that are active).
     */
    public void resume() {
        getActiveModule().activate();
    }

    /**
     * Returns all the Modules registered with this PhetApplication.
     *
     * @return all the Modules registered with this PhetApplication.
     */
    public Module[] getModules() {
        return moduleManager.getModules();
    }

    /**
     * Displays an About Dialog for the simulation.
     */
    public void showAboutDialog() {
        if ( aboutDialog == null ) {
            aboutDialog = new PhetAboutDialog( this );
            aboutDialog.addWindowListener( new WindowAdapter() {
                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    aboutDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    aboutDialog = null;
                }
            } );
            aboutDialog.show();
        }
    }

    public void setTabbedPaneType( TabbedPaneType tabbedPaneType ) {
        this.tabbedPaneType = tabbedPaneType;
    }

    /**
     * Close this PhetApplication cleanly without exiting the JVM;
     * Deactivates all modules and disposes of the associated PhetFrame for this PhetApplication.
     * TODO: verify there are no memory leaks
     */
    public void closeApplication() {
        for ( int i = 0; i < numModules(); i++ ) {
            moduleAt( i ).deactivate();
        }
        getPhetFrame().dispose();
    }

    /**
     * Enumeration class used to specify the type of tabbed panes the application is to use in
     * its Module instances
     */
    public abstract static class TabbedPaneType {
        protected TabbedPaneType() {
        }

        public abstract ITabbedModulePane createTabbedPane();
    }

    // Standard Swing JTabbedPanes
    public static final TabbedPaneType JTABBED_PANE_TYPE = new TabbedPaneType() {
        public ITabbedModulePane createTabbedPane() {
            return new JTabbedModulePane();
        }
    };
}
