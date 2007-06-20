package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;

/**
 * Author: Sam Reid
 * Jun 20, 2007, 12:26:53 AM
 */
public class TestDerivativeOffsets {
    private MotionModel motionModel;
    private SwingClock clock;

    public TestDerivativeOffsets() {
        clock = new SwingClock( 30, 1 );
        motionModel = new MotionModel( clock );
        motionModel.setPositionDriven();
    }

    private void step( int i ) {
        for( int k = 0; k < i; k++ ) {
            clock.stepClockWhilePaused();
            showState( motionModel );
        }
    }

    private void showState( MotionModel motionModel ) {
//        System.out.println( "t=" + motionModel.getTime() + ", x=" + motionModel.getPosition() + ", v=" + motionModel.getVelocity() + ", a=" + motionModel.getAcceleration() );
        System.out.println( "x.t=" + motionModel.getTime( motionModel.getXVariable() ) + ", x=" + motionModel.getPosition() +
                            ", v.t=" + motionModel.getTime( motionModel.getVVariable() ) + ", v=" + motionModel.getVelocity() +
                            ", a.t=" + motionModel.getTime( motionModel.getAVariable() ) + ", a=" + motionModel.getAcceleration() );
    }

    private void start() {
        showState( motionModel );
        step( 100 );
        motionModel.setPosition( 1.0 );
        step( 100 );
    }

    public static void main( String[] args ) {
        new TestDerivativeOffsets().start();
    }

}
