/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.CommandLineUtils;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.opticaltweezers.menu.DeveloperMenu;
import edu.colorado.phet.opticaltweezers.menu.OptionsMenu;
import edu.colorado.phet.opticaltweezers.module.DNAModule;
import edu.colorado.phet.opticaltweezers.module.MotorsModule;
import edu.colorado.phet.opticaltweezers.module.PhysicsModule;
import edu.colorado.phet.opticaltweezers.persistence.GlobalConfig;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.opticaltweezers.persistence.OTPersistenceManager;

/**
 * OpticalTweezersApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OpticalTweezersApplication extends PiccoloPhetApplication {

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
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    // Create our own pane so we can set the tab color
    public static final TabbedPaneType PHET_TABBED_PANE = new TabbedPaneType(){
        public ITabbedModulePane createTabbedPane() {
            TabbedModulePanePiccolo pane = new TabbedModulePanePiccolo();
            pane.setSelectedTabColor( OTLookAndFeel.BACKGROUND_COLOR );
            return pane;
        }
    };
    
    /**
     * Sole constructor.
     * 
     * @param args command line arguments
     */
    public OpticalTweezersApplication( PhetApplicationConfig config )
    {
        super( config, PHET_TABBED_PANE );
        DEVELOPER_CONTROLS_ENABLED = CommandLineUtils.contains( config.getCommandLineArgs(), OTConstants.DEVELOPER_ARG );
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }
    
    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the modules.
     */
    private void initModules() {
        _physicsModule = new PhysicsModule();
        addModule( _physicsModule );
        
        _dnaModule = new DNAModule();
        addModule( _dnaModule );
        
        _motorsModule = new MotorsModule();
//        addModule( _motorsModule );
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
            
            //XXX feature hidden for AAPT
//            frame.addFileMenuItem( saveItem );
//            frame.addFileMenuItem( loadItem );
//            frame.addFileMenuSeparator();
        }
        
        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        DeveloperMenu developerMenu = new DeveloperMenu();
        if ( optionsMenu.getMenuComponentCount() > 0 && isDeveloperControlsEnabled() ) {
            frame.addMenu( developerMenu );
        }
        
        // Help menu additions
        {
//            HelpMenu helpMenu = frame.getHelpMenu();
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters & getters
    //----------------------------------------------------------------------------
    
    public static boolean isDeveloperControlsEnabled() {
        return DEVELOPER_CONTROLS_ENABLED;
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
        
        config.setVersionNumber( getApplicationConfig().getVersion().toString() );
    }

    /**
     * Loads global state.
     * 
     * @param appConfig
     */
    public void load( OTConfig appConfig ) {
        
        GlobalConfig config = appConfig.getGlobalConfig();
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
                
                // Initialize look-and-feel
                final OTLookAndFeel laf = new OTLookAndFeel();
                laf.initLookAndFeel();

                PhetApplicationConfig config = new PhetApplicationConfig( args, OTConstants.FRAME_SETUP, OTResources.getResourceLoader() );
                
                // Create the application.
                OpticalTweezersApplication app = new OpticalTweezersApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
