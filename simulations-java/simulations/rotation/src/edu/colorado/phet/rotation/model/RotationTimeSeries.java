package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.DefaultTimeSeries;

/**
 * It's necessary to keep some data for performing time series based computations.
 * It's also necessary to keep all data that will be visible in playback area.
 *
 * However, if we keep all data, then the simulation will crash with OutOfMemoryException; therefore we try to prune the irrelevant data.
 *
 * Created by: Sam
 * Dec 3, 2007 at 10:57:08 PM
 */
public class RotationTimeSeries extends DefaultTimeSeries {
    public void addValue( double v, double time ) {
//        System.out.println( "time = " + time +", num data points="+getSampleCount());
        super.addValue( v, time );
        //todo: how safe is this heuristic?
        if ( time >= RotationModel.MAX_TIME * 4 ) {
//            System.out.println( "RotationTimeSeries.addValue" );
            super.removeValue( getSampleCount() / 2 );
//            System.out.println( "Removed data" );
        }
    }

}
