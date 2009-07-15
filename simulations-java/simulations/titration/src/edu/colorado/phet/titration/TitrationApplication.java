/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.titration.developer.DeveloperMenu;
import edu.colorado.phet.titration.menu.OptionsMenu;
import edu.colorado.phet.titration.module.advanced.AdvancedModule;
import edu.colorado.phet.titration.module.compare.CompareModule;
import edu.colorado.phet.titration.module.polyprotic.PolyproticModule;
import edu.colorado.phet.titration.module.titrate.TitrateModule;
import edu.colorado.phet.titration.persistence.TitrateConfig;
import edu.colorado.phet.titration.persistence.TitrationConfig;

/**
 * SimTemplateApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class TitrationApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private TitrateModule titrateModule;
    private AdvancedModule advancedModule;
    private PolyproticModule polyproticModule;
    private CompareModule compareModule;

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager persistenceManager;

    private static TabbedModulePanePiccolo tabbedModulePane;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public TitrationApplication( PhetApplicationConfig config )
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
                tabbedModulePane = new TabbedModulePanePiccolo();
                return tabbedModulePane;
            }
        };
        setTabbedPaneType( tabbedPaneType );
    }
    
    /*
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();

        titrateModule = new TitrateModule( parentFrame );
        addModule( titrateModule );
        
        advancedModule = new AdvancedModule( parentFrame );
        addModule( advancedModule );
        
        polyproticModule = new PolyproticModule( parentFrame );
        addModule( polyproticModule );
        
        compareModule = new CompareModule( parentFrame );
        addModule( compareModule );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        if ( persistenceManager == null ) {
            persistenceManager = new XMLPersistenceManager( frame );
        }

        // File menu
        {
            JMenuItem saveItem = new JMenuItem( TitrationResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( TitrationResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( TitrationResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( TitrationResources.getChar( "menu.file.load.mnemonic", 'L' ) );
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

    public PhetTabbedPane getTabbedPane() {
        return tabbedModulePane;
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /*
     * Saves the simulation's configuration.
     */
    private void save() {
        
        TitrationConfig appConfig = new TitrationConfig();
        
        // save global info
        appConfig.setVersionString( getSimInfo().getVersion().toString() );
        appConfig.setVersionMajor( getSimInfo().getVersion().getMajor() );
        appConfig.setVersionMinor( getSimInfo().getVersion().getMinor() );
        appConfig.setVersionDev( getSimInfo().getVersion().getDev() );
        appConfig.setVersionRevision( getSimInfo().getVersion().getRevision() );
        
        // save state for each module
        appConfig.setTitrateConfig( titrateModule.save() );
        appConfig.setAdvancedConfig( advancedModule.save() );
        appConfig.setPolyproticConfig( polyproticModule.save() );
        appConfig.setCompareConfig( compareModule.save() );
        
        persistenceManager.save( appConfig );
    }

    /*
     * Loads the simulation's configuration.
     */
    private void load() {
        
        Object object = persistenceManager.load();
        if ( object != null ) {
            
            if ( object instanceof TitrationConfig ) {
                TitrationConfig appConfig = (TitrationConfig) object;
                
                TitrateConfig exampleConfig = appConfig.getTitrateConfig();
                titrateModule.load( exampleConfig );
            }
            else {
                String message = TitrationResources.getString( "message.notAConfigFile" );
                String title = TitrationResources.getString( "title.error" );
                JOptionPane.showMessageDialog( getPhetFrame(), message, title, JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /* 
         * If you want to customize your application (look-&-feel, window size, etc) 
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, TitrationConstants.PROJECT_NAME, TitrationApplication.class );
    }
}
