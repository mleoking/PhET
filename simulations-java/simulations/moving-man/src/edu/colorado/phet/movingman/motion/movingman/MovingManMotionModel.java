package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Created by: Sam
 * Dec 4, 2007 at 3:37:57 PM
 */
public class MovingManMotionModel {

    private ControlGraphSeries xSeries;
    private ControlGraphSeries vSeries;
    private ControlGraphSeries aSeries;
    private SingleBodyMotionModel motionModel;
    public static final int MAX_T = 500;

    public MovingManMotionModel( ConstantDtClock clock ) {

        motionModel = new SingleBodyMotionModel( clock );
        motionModel.setPositionDriven();

        xSeries = new ControlGraphSeries( "X", Color.blue, "x", "m", new BasicStroke( 2 ), true, null, motionModel.getXVariable() );
        vSeries = new ControlGraphSeries( "V", Color.red, "v", "m/s", new BasicStroke( 2 ), true, null, motionModel.getVVariable() );
        aSeries = new ControlGraphSeries( "A", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, motionModel.getAVariable() );
        motionModel.setMaxAllowedRecordTime( MAX_T );
    }

    public ControlGraphSeries getXSeries() {
        return xSeries;
    }

    public ControlGraphSeries getVSeries() {
        return vSeries;
    }

    public ControlGraphSeries getASeries() {
        return aSeries;
    }

    public SingleBodyMotionModel getMotionModel() {
        return motionModel;
    }


}
