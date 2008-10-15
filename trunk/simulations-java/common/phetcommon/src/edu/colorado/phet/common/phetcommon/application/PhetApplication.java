/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.CommandLineUtils;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.JTabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

/**
 * The base class for PhET applications.
 */
public class PhetApplication implements ISimInfo{

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    public static final String DEVELOPER_CONTROLS_COMMAND_LINE_ARG = "-dev";//Command line argument to enable developer-only features
    private static ArrayList phetApplications = new ArrayList();

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private TabbedPaneType tabbedPaneType;
    private PhetApplicationConfig phetApplicationConfig;

    private PhetFrame phetFrame;
    private ModuleManager moduleManager;
    private PhetAboutDialog aboutDialog; // not null only when About dialog is visible

    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------

    public PhetApplication( PhetApplicationConfig config ) {
        this( config, JTABBED_PANE_TYPE );
    }

    protected PhetApplication( PhetApplicationConfig config, TabbedPaneType tabbedPaneType ) {
        this.phetApplicationConfig = config;
        this.tabbedPaneType = tabbedPaneType;

        this.moduleManager = new ModuleManager( this );
        phetFrame = createPhetFrame();
        config.getFrameSetup().initialize( phetFrame );

        // Handle command line arguments
        parseArgs( config.getCommandLineArgs() );

        phetApplications.add( this );
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
        return CommandLineUtils.contains( phetApplicationConfig.getCommandLineArgs(), DEVELOPER_CONTROLS_COMMAND_LINE_ARG );
    }

    public PhetApplicationConfig getPhetApplicationConfig() {
        return phetApplicationConfig;
    }

    /**
     * Get the last created PhetApplication.
     *
     * @return last created PhetApplication.
     */
    public static PhetApplication instance() {
        return (PhetApplication) phetApplications.get( phetApplications.size() - 1 );
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

    /**
     * Processes command line arguments. May be extended by subclasses.
     *
     * @param args
     */
    protected void parseArgs( String[] args ) {
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
                initializeModuleReferenceSizes();
                phetFrame.removeWindowFocusListener( this );
            }
        } );

        moduleManager.setActiveModule( getStartModule() );
        phetFrame.setVisible( true );

        updateLogoVisibility();
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
     */
    public String getTitle() {
        return getPhetApplicationConfig().getTitle();
    }

    /**
     * Get the description for this PhetApplication.
     *
     * @return the description.
     * @deprecated Use getProjectConfig()
     */
    public String getDescription() {
        return getPhetApplicationConfig().getDescription();
    }

    /**
     * Get the version string for this PhetApplication.
     *
     * @return the version string.
     * @deprecated Use getProjectConfig()
     */
    public String getVersion() {
        return getPhetApplicationConfig().getVersion().formatForTitleBar();
    }

    /**
     * Gets the credits for the simulations.
     *
     * @return
     * @deprecated use getProjectConfig
     */
    public String getCredits() {
        return getPhetApplicationConfig().getCredits();
    }

    /**
     * Adds modules.
     *
     * @param m the array of modules to add
     */
    public void addModules( Module[] m ) {
        for ( int i = 0; i < m.length; i++ ) {
            addModule( m[i] );
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
