// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.eatingandexercise.developer.DeveloperFrame;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseModule;

public class EatingAndExerciseApplication extends PiccoloPhetApplication {

    private EatingAndExerciseModule eatingAndExerciseModule;

    public EatingAndExerciseApplication( PhetApplicationConfig config ) {
        super( config );
        eatingAndExerciseModule = new EatingAndExerciseModule( getPhetFrame() );
        addModule( eatingAndExerciseModule );
        initMenubar();
    }

    protected PhetFrame createPhetFrame() {
        return new PhetFrameWorkaround( this );
    }

    private void initMenubar() {
        final PhetFrame frame = getPhetFrame();

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