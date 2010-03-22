/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main application for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * 
 */
public class AcidBaseSolutionsApplication extends PiccoloPhetApplication {

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
    public AcidBaseSolutionsApplication( PhetApplicationConfig config )
    {
        super( config );
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
        
        Frame parentFrame = getPhetFrame();

        //TODO add modules
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        // File->Save/Load
        final PhetFrame frame = getPhetFrame();
        frame.addFileSaveLoadMenuItems();
        if ( persistenceManager == null ) {
            persistenceManager = new XMLPersistenceManager( frame );
        }
    }

    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    /**
     * Saves the simulation's configuration.
     */
    @Override
    public void save() {
       //TODO
    }

    /**
     * Loads the simulation's configuration.
     */
    @Override
    public void load() {
        //TODO
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, ABSConstants.PROJECT_NAME, AcidBaseSolutionsApplication.class );
    }
}
