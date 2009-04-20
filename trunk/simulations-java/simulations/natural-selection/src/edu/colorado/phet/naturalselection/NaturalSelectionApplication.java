/* Copyright 2007, University of Colorado */

package edu.colorado.phet.naturalselection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.ITabbedModulePane;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;
import edu.colorado.phet.naturalselection.developer.DeveloperMenu;
import edu.colorado.phet.naturalselection.menu.OptionsMenu;
import edu.colorado.phet.naturalselection.module.example.ExampleModule;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModule;
import edu.colorado.phet.naturalselection.persistence.ExampleConfig;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;

/**
 * SimTemplateApplication is the main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NaturalSelectionApplication extends PiccoloPhetApplication {

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
     * @param config the configuration for this application
     */
    public NaturalSelectionApplication( PhetApplicationConfig config ) {
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
        TabbedPaneType tabbedPaneType = new TabbedPaneType() {
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

        Module tempModule = new NaturalSelectionModule( parentFrame );
        addModule( tempModule );

        // for now, have another module for this
        // TODO: remove that control-panel like thing on the right when there is only one module
        addModule( new NaturalSelectionModule( parentFrame ) );


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
            JMenuItem saveItem = new JMenuItem( NaturalSelectionResources.getString( "menu.file.save" ) );
            saveItem.setMnemonic( NaturalSelectionResources.getChar( "menu.file.save.mnemonic", 'S' ) );
            saveItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    save();
                }
            } );

            JMenuItem loadItem = new JMenuItem( NaturalSelectionResources.getString( "menu.file.load" ) );
            loadItem.setMnemonic( NaturalSelectionResources.getChar( "menu.file.load.mnemonic", 'L' ) );
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

    public void setControlPanelBackground( Color color ) {
        Module[] modules = getModules();
        for ( int i = 0; i < modules.length; i++ ) {
            modules[i].setControlPanelBackground( color );
            modules[i].setClockControlPanelBackground( color );
            modules[i].setHelpPanelBackground( color );
        }
    }

    public Color getControlPanelBackground() {
        return getModule( 0 ).getControlPanel().getBackground();
    }

    public PhetTabbedPane getTabbedPane() {
        return _tabbedModulePane;
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /*
     * Saves the simulation's configuration.
     */

    private void save() {

        NaturalSelectionConfig appConfig = new NaturalSelectionConfig();

        appConfig.setVersionString( getSimInfo().getVersion().toString() );
        appConfig.setVersionMajor( getSimInfo().getVersion().getMajor() );
        appConfig.setVersionMinor( getSimInfo().getVersion().getMinor() );
        appConfig.setVersionDev( getSimInfo().getVersion().getDev() );
        appConfig.setVersionRevision( getSimInfo().getVersion().getRevision() );

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

            if ( object instanceof NaturalSelectionConfig ) {
                NaturalSelectionConfig appConfig = (NaturalSelectionConfig) object;

                ExampleConfig exampleConfig = appConfig.getExampleConfig();
                _exampleModule.load( exampleConfig );
            }
            else {
                String message = NaturalSelectionResources.getString( "message.notAConfigFile" );
                String title = NaturalSelectionResources.getString( "title.error" );
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
        testReflection();

        new PhetApplicationLauncher().launchSim( args, NaturalSelectionConstants.PROJECT_NAME, NaturalSelectionApplication.class );
    }

    private static void testReflection() {
        try {
            Class c = Class.forName( "edu.colorado.phet.naturalselection.NaturalSelectionApplication" );
            System.out.println( "reflection gave: " + c.getMethods().length + " methods" );
        }
        catch( Throwable t ) {
            t.printStackTrace();
        }
    }
}
