// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.colorado.phet.gravityandorbits.simsharing.SimHistoryPlayback;
import edu.colorado.phet.gravityandorbits.simsharing.SimSharingStudentClient;
import edu.colorado.phet.gravityandorbits.simsharing.SimSharingTeacherClient;

/**
 * The main application for Gravity and Orbits.
 *
 * @see GravityAndOrbitsModule
 */
public class GravityAndOrbitsApplication extends PiccoloPhetApplication {
    public static final String PROJECT_NAME = "gravity-and-orbits";
    public static final PhetResources RESOURCES = new PhetResources( GravityAndOrbitsApplication.PROJECT_NAME );
    private final GravityAndOrbitsModule gravityAndOrbitsModule;

    public GravityAndOrbitsApplication( PhetApplicationConfig config ) {
        super( config );
        gravityAndOrbitsModule = new GravityAndOrbitsModule( getPhetFrame(), config.getCommandLineArgs() );
        addModule( gravityAndOrbitsModule );
        initMenubar();
        getPhetFrame().setTitle( getPhetFrame().getTitle() + ": " + Arrays.toString( config.getCommandLineArgs() ) ); //simsharing, append command line args to title

        String[] commandLineArgs = config.getCommandLineArgs();
        if ( Arrays.asList( commandLineArgs ).contains( "-teacher" ) ) {
            gravityAndOrbitsModule.setTeacherMode( true );
            try {
                new SimSharingTeacherClient( this, getPhetFrame() ).start();
            }
            catch ( AWTException e ) {
                e.printStackTrace();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            if ( Arrays.asList( commandLineArgs ).contains( "-history" ) ) {//load and play back history
                int index = Arrays.asList( commandLineArgs ).indexOf( "-history" );
                String historyFile = commandLineArgs[index + 1];
                SimHistoryPlayback.playHistory( this, new File( historyFile ) );
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

    private void initMenubar() {

        // File->Save/Load
        final PhetFrame frame = getPhetFrame();
        frame.addFileSaveLoadMenuItems();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, PROJECT_NAME, GravityAndOrbitsApplication.class );
    }
}
