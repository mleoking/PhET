/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.glaciers.menu.DeveloperMenu;
import edu.colorado.phet.glaciers.menu.OptionsMenu;
import edu.colorado.phet.glaciers.module.advanced.AdvancedModule;
import edu.colorado.phet.glaciers.module.intro.IntroModule;
import edu.colorado.phet.glaciers.persistence.AdvancedConfig;
import edu.colorado.phet.glaciers.persistence.GlaciersConfig;
import edu.colorado.phet.glaciers.persistence.IntroConfig;

/**
 * GlaciersApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private IntroModule _introModule;
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
        
        _introModule = new IntroModule( parentFrame );
        addModule( _introModule );

        _advancedModule = new AdvancedModule( parentFrame );
        addModule( _advancedModule );
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
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEvolutionStateDialogVisible( boolean visible ) {
        _introModule.setEvolutionStateDialogVisible( visible );
        _advancedModule.setEvolutionStateDialogVisible( visible );
    }
    
    public void setModelConstantsDialogVisible( boolean visible ) {
        _introModule.setModelConstantsDialogVisible( visible );
        _advancedModule.setModelConstantsDialogVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /*
     * Saves the simulation's configuration.
     */
    private void save() {
        
        GlaciersConfig appConfig = new GlaciersConfig();
        
        appConfig.setVersionString( getSimInfo().getVersion().toString() );
        appConfig.setVersionMajor( getSimInfo().getVersion().getMajor() );
        appConfig.setVersionMinor( getSimInfo().getVersion().getMinor() );
        appConfig.setVersionDev( getSimInfo().getVersion().getDev() );
        appConfig.setVersionRevision( getSimInfo().getVersion().getRevision() );
        
        IntroConfig basicConfig = _introModule.save();
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
                
                IntroConfig basicConfig = appConfig.getBasicConfig();
                _introModule.load( basicConfig );
                
                AdvancedConfig advancedConfig = appConfig.getAdvancedConfig();
                _advancedModule.load( advancedConfig );
            }
            else {
                JOptionPane.showMessageDialog( getPhetFrame(), GlaciersStrings.MESSAGE_NOT_A_CONFIG_FILE, GlaciersStrings.TITLE_ERROR, JOptionPane.ERROR_MESSAGE );
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
     */
    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, GlaciersConstants.PROJECT_NAME, GlaciersApplication.class );
    }
}
