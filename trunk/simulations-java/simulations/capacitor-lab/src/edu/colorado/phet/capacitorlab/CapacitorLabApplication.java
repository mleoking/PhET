/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

import java.awt.Frame;

import javax.swing.Box;

import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModule;
import edu.colorado.phet.capacitorlab.module.introduction.IntroductionModule;
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
public class CapacitorLabApplication extends PiccoloPhetApplication {

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager persistenceManager;

    private IntroductionModule introductionModule;
    private DielectricModule dielectricModule;
    
    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public CapacitorLabApplication( PhetApplicationConfig config )
    {
        super( config );
        initModules();
        initMenubar( config.getCommandLineArgs() );
    }

    /*
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();
        boolean dev = isDeveloperControlsEnabled();
        
        // add modules
        introductionModule = new IntroductionModule( parentFrame, dev );
        addModule( introductionModule );
        dielectricModule = new DielectricModule( parentFrame, dev );
        addModule( dielectricModule );
        
        // make all control panels the same width
        int maxWidth = 0;
        for ( Module module : getModules() ) {
            maxWidth = Math.max( maxWidth, module.getControlPanel().getPreferredSize().width );
        }
        for ( Module module : getModules() ) {
            module.getControlPanel().addControlFullWidth( Box.createHorizontalStrut( maxWidth ) );
        }
        
        setStartModule( dielectricModule );
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
        new PhetApplicationLauncher().launchSim( args, CLConstants.PROJECT_NAME, CapacitorLabApplication.class );
    }
}
