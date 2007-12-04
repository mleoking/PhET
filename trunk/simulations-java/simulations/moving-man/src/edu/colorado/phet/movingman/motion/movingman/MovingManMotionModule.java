package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:42:37 PM
 */
public class MovingManMotionModule extends Module {
    private ConstantDtClock clock;
    private SingleBodyMotionModel motionModel;
    public static final int MAX_T = 500;
    private ControlGraphSeries xSeries;
    private ControlGraphSeries vSeries;
    private ControlGraphSeries aSeries;

    public MovingManMotionModule( ConstantDtClock clock ) {
        super( "Moving Man", clock );

        motionModel = new SingleBodyMotionModel( clock );
        motionModel.setPositionDriven();
        this.clock = clock;

        xSeries = new ControlGraphSeries( "X", Color.blue, "x", "m", new BasicStroke( 2 ), true, null, motionModel.getXVariable() );
        vSeries = new ControlGraphSeries( "V", Color.red, "v", "m/s", new BasicStroke( 2 ), true, null, motionModel.getVVariable() );
        aSeries = new ControlGraphSeries( "A", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, motionModel.getAVariable() );

        final MovingManMotionSimPanel simPanel = new MovingManMotionSimPanel( this );
        setSimulationPanel( simPanel );

        motionModel.setMaxAllowedRecordTime( MAX_T );
        System.out.println( "motionModel.getTimeSeriesModel().getMode() = " + motionModel.getTimeSeriesModel().getMode() + " ispaused=" + motionModel.getTimeSeriesModel().isPaused() );

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

    public SingleBodyMotionModel getSingleBodyMotionModel() {
        return motionModel;
    }
}
