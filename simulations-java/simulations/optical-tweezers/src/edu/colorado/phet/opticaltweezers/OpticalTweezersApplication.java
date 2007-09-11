/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.CommandLineUtils;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.opticaltweezers.menu.DeveloperMenu;
import edu.colorado.phet.opticaltweezers.menu.OptionsMenu;
import edu.colorado.phet.opticaltweezers.module.OTAbstractModule;
import edu.colorado.phet.opticaltweezers.module.dna.DNAModule;
import edu.colorado.phet.opticaltweezers.module.motors.MotorsModule;
import edu.colorado.phet.opticaltweezers.module.physics.PhysicsModule;
import edu.colorado.phet.opticaltweezers.persistence.GlobalConfig;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.persistence.OTPersistenceManager;

/**
 * OpticalTweezersApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OpticalTweezersApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static boolean DEVELOPER_CONTROLS_ENABLED;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhysicsModule _physicsModule;
    private DNAModule _dnaModule;
    private MotorsModule _motorsModule;

    // PersistanceManager handles loading/saving application configurations.
    private OTPersistenceManager _persistenceManager;

    private static TabbedModulePanePiccolo _tabbedModulePane;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param args command line arguments
     */
    public OpticalTweezersApplication( PhetApplicationConfig config )
    {
        super( config );
        DEVELOPER_CONTROLS_ENABLED = CommandLineUtils.contains( config.getCommandLineArgs(), OTConstants.DEVELOPER_ARG );
        initTabbedPane();
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the tabbed pane.
     */
    private void initTabbedPane() {

        // Create our own tabbed pane type so we can set the tab color
        TabbedPaneType tabbedPaneType = new TabbedPaneType(){
            public ITabbedModulePane createTabbedPane() {
                _tabbedModulePane = new TabbedModulePanePiccolo();
                _tabbedModulePane.setSelectedTabColor( OTConstants.SELECTED_TAB_COLOR );
                return _tabbedModulePane;
            }
        };
        setTabbedPaneType( tabbedPaneType );
    }

    /*
     * Initializes the modules.
     */
    private void initModules() {

        _physicsModule = new PhysicsModule();
        addModule( _physicsModule );

        _dnaModule = new DNAModule();
        addModule( _dnaModule );

        _motorsModule = new MotorsModule();
        addModule( _motorsModule );

        setControlPanelBackground( OTConstants.CONTROL_PANEL_COLOR );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        if ( _persistenceManager == null ) {
            _persistenceManager = new OTPersistenceManager( this );
        }

        // File menu
        {
            JMenuItem saveItem = new JMenuItem( OTResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( OTResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _persistenceManager.save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( OTResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( OTResources.getChar( "menu.file.load.mnemonic", 'L' ) );
            loadItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    _persistenceManager.load();
                }
            } );

            frame.addFileMenuItem( saveItem );
            frame.addFileMenuItem( loadItem );
            frame.addFileMenuSeparator();
        }

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        DeveloperMenu developerMenu = new DeveloperMenu( this );
        if ( developerMenu.getMenuComponentCount() > 0 && isDeveloperControlsEnabled() ) {
            frame.addMenu( developerMenu );
        }
    }

    //----------------------------------------------------------------------------
    // Setters & getters
    //----------------------------------------------------------------------------

    public static boolean isDeveloperControlsEnabled() {
        return DEVELOPER_CONTROLS_ENABLED;
    }

    public void setSelectedTabColor( Color color ) {
        _tabbedModulePane.setSelectedTabColor( color );
    }

    public Color getSelectedTabColor() {
        return _tabbedModulePane.getSelectedTabColor();
    }

    public void setControlPanelBackground( Color color ) {
        Module[] modules = getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            if ( modules[i] instanceof OTAbstractModule ) {
                OTAbstractModule module = (OTAbstractModule) modules[i];
                module.setControlPanelBackground( color );
            }
        }
    }

    public Color getControlPanelBackground() {
        return ( (OTAbstractModule) getModule( 0 ) ).getControlPanelBackground();
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves global state.
     *
     * @param appConfig
     */
    public void save( OTConfig appConfig ) {

        GlobalConfig config = appConfig.getGlobalConfig();

        config.setVersionString( getApplicationConfig().getVersion().toString() );
        config.setVersionMajor( getApplicationConfig().getVersion().getMajor() );
        config.setVersionMinor( getApplicationConfig().getVersion().getMinor() );
        config.setVersionDev( getApplicationConfig().getVersion().getDev() );
        config.setVersionRevision( getApplicationConfig().getVersion().getRevision() );
    }

    /**
     * Loads global state.
     *
     * @param appConfig
     */
    public void load( OTConfig appConfig ) {
        // GlobalConfig currently contains nothing that needs to be loaded
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point.
     *
     * @param args command line arguments
     * @throws InvocationTargetException
     * @throws InterruptedException
     */
    public static void main( final String[] args ) {

        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the
         * event dispatch thread. Since we don't have an easy way to separate Swing and
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                PhetApplicationConfig config = new PhetApplicationConfig( args, OTConstants.FRAME_SETUP, OTResources.getResourceLoader() );

                // Create the application.
                OpticalTweezersApplication app = new OpticalTweezersApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
