package edu.colorado.phet.movingman;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.movingman.motion.MotionProjectLookAndFeel;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModule;
import edu.colorado.phet.movingman.motion.movingman.OptionsMenu;
import edu.colorado.phet.simtemplate.developer.DeveloperMenu;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:38:34 AM
 */
public class MovingManApplication extends PiccoloPhetApplication {
    public static final double FRAME_RATE_HZ = 20;//20fps
    public static final double FRAME_DELAY_SEC = 1.0 / FRAME_RATE_HZ;//DT
    public static final double FRAME_DELAY_MILLIS = 1000 * FRAME_DELAY_SEC;

    public MovingManApplication( PhetApplicationConfig config ) {
        super( config );
        MovingManMotionModule m = new MovingManMotionModule( new ConstantDtClock( (int) FRAME_DELAY_MILLIS, FRAME_DELAY_SEC ) );
        addModule( m );

        getPhetFrame().addMenu( new OptionsMenu( getPhetFrame(), m ) );

        if ( isDeveloperControlsEnabled() ) {
            getPhetFrame().addMenu( new MovingManDeveloperMenu() );
        }
    }

    public static void main( final String[] args ) {

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new MovingManApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, "moving-man" );
        appConfig.setLookAndFeel( new MotionProjectLookAndFeel() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
