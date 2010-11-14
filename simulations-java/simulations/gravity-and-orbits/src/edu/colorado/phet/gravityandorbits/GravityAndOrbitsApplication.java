/* Copyright 2007, University of Colorado */

package edu.colorado.phet.gravityandorbits;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.colorado.phet.gravityandorbits.simsharing.SimSharingStudentClient;
import edu.colorado.phet.gravityandorbits.simsharing.SimSharingTeacherClient;

/**
 * The main application for this simulation.
 */
public class GravityAndOrbitsApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager persistenceManager;
    private GravityAndOrbitsModule gravityAndOrbitsModule;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public GravityAndOrbitsApplication( PhetApplicationConfig config ) {
        super( config );
        gravityAndOrbitsModule = new GravityAndOrbitsModule( getPhetFrame(), config.getCommandLineArgs() ) {{
            getModulePanel().setLogoPanel( null );
        }};
        addModule( gravityAndOrbitsModule );
        initMenubar();
        getPhetFrame().setTitle( config.getName() + ": " + Arrays.toString( config.getCommandLineArgs() ) );//simsharing

        String[] commandLineArgs = config.getCommandLineArgs();
        if ( Arrays.asList( commandLineArgs ).contains( "-teacher" ) ) {
            gravityAndOrbitsModule.getGravityAndOrbitsModel().teacherMode = true;
            try {
                new SimSharingTeacherClient( this, getPhetFrame() ).start();
            }
            catch ( AWTException e ) {
                e.printStackTrace();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        else if ( Arrays.asList( commandLineArgs ).contains( "-student" ) ) {
            try {
                new SimSharingStudentClient( this, getPhetFrame() ).start();
            }
            catch ( AWTException e ) {
                e.printStackTrace();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public GravityAndOrbitsModule getGravityAndOrbitsModule() {
        return gravityAndOrbitsModule;
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
        new PhetApplicationLauncher().launchSim( args, GravityAndOrbitsConstants.PROJECT_NAME, GravityAndOrbitsApplication.class );
    }
}
