package edu.colorado.phet.movingman;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.movingman.motion.MotionProjectLookAndFeel;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModule;
import edu.colorado.phet.movingman.motion.movingman.OptionsMenu;

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
    }

    public static void main( final String[] args ) {
        
        SimStrings.getInstance().addStrings( "moving-man/localization/moving-man-strings" );
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new MovingManApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, "moving-man" );
        appConfig.setLookAndFeel( new MotionProjectLookAndFeel() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
