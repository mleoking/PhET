/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.CommandLineUtils;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.nuclearphysics2.SimTemplateApplication;
import edu.colorado.phet.nuclearphysics2.TemplateConstants;
import edu.colorado.phet.nuclearphysics2.TemplateResources;
import edu.colorado.phet.simtemplate.developer.DeveloperMenu;
import edu.colorado.phet.simtemplate.menu.OptionsMenu;
import edu.colorado.phet.simtemplate.module.example.ExampleModule;
import edu.colorado.phet.simtemplate.persistence.ExampleConfig;
import edu.colorado.phet.simtemplate.persistence.TemplateConfig;

/**
 * TemplateApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class SimTemplateApplication extends PhetApplication {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static boolean DEVELOPER_CONTROLS_ENABLED;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ExampleModule _exampleModule;

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
    public SimTemplateApplication( PhetApplicationConfig config )
    {
        super( config );
        DEVELOPER_CONTROLS_ENABLED = CommandLineUtils.contains( config.getCommandLineArgs(), TemplateConstants.DEVELOPER_ARG );
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
                _tabbedModulePane.setSelectedTabColor( TemplateConstants.SELECTED_TAB_COLOR );
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

        _exampleModule = new ExampleModule( parentFrame );
        addModule( _exampleModule );
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
            JMenuItem saveItem = new JMenuItem( TemplateResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( TemplateResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( TemplateResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( TemplateResources.getChar( "menu.file.load.mnemonic", 'L' ) );
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

    public void setSelectedTabColor( Color color ) {
        if ( _tabbedModulePane != null ) {
            _tabbedModulePane.setSelectedTabColor( color );
        }
    }

    public Color getSelectedTabColor() {
        Color color = Color.WHITE; 
        if ( _tabbedModulePane != null ) {
            color = _tabbedModulePane.getSelectedTabColor();
        }
        return color;
    }

    public void setControlPanelBackground( Color color ) {
        Module[] modules = getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            modules[i].setControlPanelBackground( color );
            modules[i].setClockControlPanelBackground( color );
            modules[i].setHelpPanelBackground( color );
        }
    }

    public Color getControlPanelBackground() {
        return ( (Module) getModule( 0 ) ).getControlPanel().getBackground();
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /*
     * Saves the simulation's configuration.
     */
    private void save() {
        
        TemplateConfig appConfig = new TemplateConfig();
        
        appConfig.setVersionString( getApplicationConfig().getVersion().toString() );
        appConfig.setVersionMajor( getApplicationConfig().getVersion().getMajor() );
        appConfig.setVersionMinor( getApplicationConfig().getVersion().getMinor() );
        appConfig.setVersionDev( getApplicationConfig().getVersion().getDev() );
        appConfig.setVersionRevision( getApplicationConfig().getVersion().getRevision() );
        
        ExampleConfig exampleConfig = _exampleModule.save();
        appConfig.setExampleConfig( exampleConfig );
        
        _persistenceManager.save( appConfig );
    }

    /*
     * Loads the simulation's configuration.
     */
    private void load() {
        
        Object object = _persistenceManager.load();
        if ( object != null ) {
            
            if ( object instanceof TemplateConfig ) {
                TemplateConfig appConfig = (TemplateConfig) object;
                
                ExampleConfig exampleConfig = appConfig.getExampleConfig();
                _exampleModule.load( exampleConfig );
            }
            else {
                String message = TemplateResources.getString( "message.notAConfigFile" );
                String title = TemplateResources.getString( "title.error" );
                DialogUtils.showErrorDialog( getPhetFrame(), message, title );
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

                PhetApplicationConfig config = new PhetApplicationConfig( args, TemplateConstants.FRAME_SETUP, TemplateResources.getResourceLoader() );

                // Create the application.
                SimTemplateApplication app = new SimTemplateApplication( config );

                // Start the application.
                app.startApplication();
            }
        } );
    }
}
