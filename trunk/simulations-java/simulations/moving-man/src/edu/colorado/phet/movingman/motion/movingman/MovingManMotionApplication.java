package edu.colorado.phet.movingman.motion.movingman;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSetNode;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.movingman.MovingManApplication;
import edu.colorado.phet.movingman.motion.MotionProjectLookAndFeel;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:38:34 AM
 */
public class MovingManMotionApplication extends PhetApplication {
    private GraphSetNode graphSetNode;
    private MovingManNode movingManNode;
    public static final double FRAME_RATE_HZ = 20;//20fps
    public static final double FRAME_DELAY_SEC = 1.0 / FRAME_RATE_HZ;//DT
    public static final double FRAME_DELAY_MILLIS = 1000 * FRAME_DELAY_SEC;

    public MovingManMotionApplication( String[] args ) {
        super( new PhetApplicationConfig( args, new FrameSetup.TopCenter( 1024, 768 ), PhetResources.forProject( "moving-man" ), "mm-motion" ) );
        MovingManMotionModule m = new MovingManMotionModule( new ConstantDtClock( (int) FRAME_DELAY_MILLIS, FRAME_DELAY_SEC ) );
        addModule( m );

        getPhetFrame().addMenu( new OptionsMenu( getPhetFrame(), m ) );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                MotionProjectLookAndFeel.init();
                SimStrings.getInstance().addStrings( MovingManApplication.localizedStringsPath );
                new MovingManMotionApplication( args ).startApplication();
            }
        } );
    }
}
