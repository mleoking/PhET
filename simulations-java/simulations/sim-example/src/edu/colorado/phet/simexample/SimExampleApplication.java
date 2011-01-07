// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.simexample;

import java.awt.Frame;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.simexample.module.example.ExampleModule;
import edu.colorado.phet.simexample.persistence.ExampleConfig;
import edu.colorado.phet.simexample.persistence.SimExampleConfig;

/**
 * The main application for this simulation.
 */
public class SimExampleApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ExampleModule exampleModule;

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager persistenceManager;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public SimExampleApplication( PhetApplicationConfig config )
    {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();

        exampleModule = getFirstModule(parentFrame);
        addModule( exampleModule );
        
        Module secondModule = new ExampleModule( parentFrame );
        secondModule.setName( SimExampleStrings.TITLE_ANOTHER_EXAMPLE_MODULE );
        addModule( secondModule );
    }

    protected ExampleModule getFirstModule(Frame parentFrame) {
        return new ExampleModule( parentFrame );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        // File->Save/Load
        final PhetFrame frame = getPhetFrame();
        frame.addFileSaveLoadMenuItems();
        if ( persistenceManager == null ) {
            persistenceManager = new XMLPersistenceManager( frame );
        }

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves the simulation's configuration.
     */
    @Override
    public void save() {
        
        SimExampleConfig appConfig = new SimExampleConfig();
        
        appConfig.setVersionString( getSimInfo().getVersion().toString() );
        appConfig.setVersionMajor( getSimInfo().getVersion().getMajor() );
        appConfig.setVersionMinor( getSimInfo().getVersion().getMinor() );
        appConfig.setVersionDev( getSimInfo().getVersion().getDev() );
        appConfig.setVersionRevision( getSimInfo().getVersion().getRevision() );
        
        ExampleConfig exampleConfig = exampleModule.save();
        appConfig.setExampleConfig( exampleConfig );
        
        persistenceManager.save( appConfig );
    }

    /**
     * Loads the simulation's configuration.
     */
    @Override
    public void load() {
        
        Object object = persistenceManager.load();
        if ( object != null ) {
            
            if ( object instanceof SimExampleConfig ) {
                SimExampleConfig appConfig = (SimExampleConfig) object;
                
                ExampleConfig exampleConfig = appConfig.getExampleConfig();
                exampleModule.load( exampleConfig );
            }
            else {
                String message = SimExampleStrings.MESSAGE_NOT_A_CONFIG;
                PhetOptionPane.showErrorDialog( getPhetFrame(), message );
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
        new PhetApplicationLauncher().launchSim( args, SimExampleConstants.PROJECT_NAME, SimExampleApplication.class );
    }
}
