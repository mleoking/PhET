package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.DefaultTimeSeries;

/**
 * Created by: Sam
 * Dec 3, 2007 at 10:57:08 PM
 */
public class RotationTimeSeries extends DefaultTimeSeries {
    public void addValue( double v, double time ) {
//        System.out.println( "time = " + time +", num data points="+getSampleCount());
        super.addValue( v, time );
        //1st pass will have a glitch in computation near MAX_TIME
        //todo: how safe is this heuristic?
        if ( time >= RotationModel.MAX_TIME * 4 ) {
//            System.out.println( "RotationTimeSeries.addValue" );
            super.removeValue( getSampleCount() / 2 );
//            System.out.println( "Removed data" );
        }
    }

}
