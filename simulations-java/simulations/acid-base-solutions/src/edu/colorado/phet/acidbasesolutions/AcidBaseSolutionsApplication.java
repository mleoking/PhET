/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import javax.swing.Box;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.module.customsolution.CustomSolutionModule;
import edu.colorado.phet.acidbasesolutions.module.testsolution.TestSolutionModule;
import edu.colorado.phet.common.phetcommon.application.Module;
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
        
        addModule( new TestSolutionModule() );
        addModule( new CustomSolutionModule() );
        
        // make all control panels the same width
        int maxWidth = 0;
        for ( Module module : getModules() ) {
            maxWidth = Math.max( maxWidth, module.getControlPanel().getPreferredSize().width );
        }
        for ( Module module : getModules() ) {
            module.getControlPanel().addControlFullWidth( Box.createHorizontalStrut( maxWidth ) );
        }
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
       //TODO implement
    }

    /**
     * Loads the simulation's configuration.
     */
    @Override
    public void load() {
        //TODO implement
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, ABSConstants.PROJECT_NAME, AcidBaseSolutionsApplication.class );
    }
}
