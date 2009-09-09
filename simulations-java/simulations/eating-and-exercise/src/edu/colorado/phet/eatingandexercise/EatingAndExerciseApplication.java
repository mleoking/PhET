/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.eatingandexercise;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.persistence.XMLPersistenceManager;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.eatingandexercise.developer.DeveloperFrame;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModule;

public class EatingAndExerciseApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private EatingAndExerciseModule eatingAndExerciseModule;

    // PersistanceManager is used to save/load simulation configurations.
    private XMLPersistenceManager _persistenceManager;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public EatingAndExerciseApplication( PhetApplicationConfig config ) {
        super( config );
        eatingAndExerciseModule = new EatingAndExerciseModule( getPhetFrame() );
        addModule( eatingAndExerciseModule );
        initMenubar( config.getCommandLineArgs() );
    }

    protected PhetFrame createPhetFrame() {
        return new PhetFrameWorkaround( this );
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the menubar.
     */
    private void initMenubar( String[] args ) {

        final PhetFrame frame = getPhetFrame();

        if ( _persistenceManager == null ) {
            _persistenceManager = new XMLPersistenceManager( frame );
        }

        // Developer menu
        JMenu developerMenu = getPhetFrame().getDeveloperMenu();
        JMenuItem menuItem = new JMenuItem( "Show Model Controls..." );
        menuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new DeveloperFrame().setVisible( true );
            }
        } );
        developerMenu.add( menuItem );

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
        return getModule( 0 ).getSimulationPanel().getBackground();
    }

    public void startApplication() {
        super.startApplication();
        eatingAndExerciseModule.applicationStarted();
    }

    public static void main( final String[] args ) {
        PhetApplicationConfig config = new PhetApplicationConfig( args, EatingAndExerciseConstants.PROJECT_NAME );
        config.setLookAndFeel( new EatingAndExerciseLookAndFeel() );
        new PhetApplicationLauncher().launchSim( config, EatingAndExerciseApplication.class );
    }
}
