/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.CommandLineUtils;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.glaciers.menu.DeveloperMenu;
import edu.colorado.phet.glaciers.menu.OptionsMenu;
import edu.colorado.phet.glaciers.module.advanced.AdvancedModule;
import edu.colorado.phet.glaciers.module.basic.BasicModule;
import edu.colorado.phet.glaciers.persistence.AdvancedConfig;
import edu.colorado.phet.glaciers.persistence.BasicConfig;
import edu.colorado.phet.glaciers.persistence.GlaciersConfig;

/**
 * GlaciersApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static boolean DEVELOPER_CONTROLS_ENABLED;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BasicModule _basicModule;
    private AdvancedModule _advancedModule;

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager _persistenceManager;

    private static TabbedModulePanePiccolo _tabbedModulePane;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param args command line arguments
     */
    public GlaciersApplication( PhetApplicationConfig config )
    {
        super( config );
        DEVELOPER_CONTROLS_ENABLED = CommandLineUtils.contains( config.getCommandLineArgs(), GlaciersConstants.DEVELOPER_ARG );
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
                _tabbedModulePane.setSelectedTabColor( GlaciersConstants.SELECTED_TAB_COLOR );
                return _tabbedModulePane;
            }
        };
        setTabbedPaneType( tabbedPaneType );
    }
    
    /*
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();
        
        _basicModule = new BasicModule( parentFrame );
        addModule( _basicModule );

//        _advancedModule = new AdvancedModule( parentFrame );
//        addModule( _advancedModule );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( frame );
        }

        // File menu
        {
            JMenuItem saveItem = new JMenuItem( GlaciersStrings.MENU_FILE_SAVE );
            saveItem.setMnemonic( GlaciersStrings.MENU_FILE_SAVE_MNEMONIC );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( GlaciersStrings.MENU_FILE_LOAD );
            loadItem.setMnemonic( GlaciersStrings.MENU_FILE_LOAD_MNEMONIC );
            loadItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    load();
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

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /*
     * Saves the simulation's configuration.
     */
    private void save() {
        
        GlaciersConfig appConfig = new GlaciersConfig();
        
        appConfig.setVersionString( getApplicationConfig().getVersion().toString() );
        appConfig.setVersionMajor( getApplicationConfig().getVersion().getMajor() );
        appConfig.setVersionMinor( getApplicationConfig().getVersion().getMinor() );
        appConfig.setVersionDev( getApplicationConfig().getVersion().getDev() );
        appConfig.setVersionRevision( getApplicationConfig().getVersion().getRevision() );
        
        BasicConfig basicConfig = _basicModule.save();
        appConfig.setBasicConfig( basicConfig );
        
        AdvancedConfig advancedConfig = _advancedModule.save();
        appConfig.setAdvancedConfig( advancedConfig );
        
        _persistenceManager.save( appConfig );
    }

    /*
     * Loads the simulation's configuration.
     */
    private void load() {
        
        Object object = _persistenceManager.load();
        if ( object != null ) {
            
            if ( object instanceof GlaciersConfig ) {
                GlaciersConfig appConfig = (GlaciersConfig) object;
                
                BasicConfig basicConfig = appConfig.getBasicConfig();
                _basicModule.load( basicConfig );
                
                AdvancedConfig advancedConfig = appConfig.getAdvancedConfig();
                _advancedModule.load( advancedConfig );
            }
            else {
                DialogUtils.showErrorDialog( getPhetFrame(), GlaciersStrings.MESSAGE_NOT_A_CONFIG_FILE, GlaciersStrings.TITLE_ERROR );
            }
        }
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

                PhetApplicationConfig config = new PhetApplicationConfig( args, GlaciersConstants.FRAME_SETUP, GlaciersResources.getResourceLoader() );

                // Create the application.
                GlaciersApplication app = new GlaciersApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
