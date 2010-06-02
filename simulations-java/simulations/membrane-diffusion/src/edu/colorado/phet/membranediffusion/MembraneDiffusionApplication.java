/* Copyright 2007, University of Colorado */

package edu.colorado.phet.membranediffusion;

import java.awt.Frame;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.membranediffusion.module.MembraneDiffusionModule;

/**
 * The main application for this simulation.
 */
public class MembraneDiffusionApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

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
    public MembraneDiffusionApplication( PhetApplicationConfig config )
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

        Module firstModule = new MembraneDiffusionModule( parentFrame );
        addModule( firstModule );
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
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /* 
         * If you want to customize your application (look-&-feel, window size, etc) 
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, MembraneDiffusionConstants.PROJECT_NAME, MembraneDiffusionApplication.class );
    }
}
