package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Created by: Sam
 * Dec 4, 2007 at 3:37:57 PM
 */
public class MovingManMotionModel extends MotionModel implements UpdateableObject, IMovingManModel {
    private ITemporalVariable x = new DefaultTemporalVariable();
    private ITemporalVariable v = new DefaultTemporalVariable();
    private ITemporalVariable a = new DefaultTemporalVariable();

    private ControlGraphSeries xSeries = new ControlGraphSeries( "X", Color.blue, "x", "m", new BasicStroke( 2 ), true, null, x );
    private ControlGraphSeries vSeries = new ControlGraphSeries( "V", Color.red, "v", "m/s", new BasicStroke( 2 ), true, null, v );
    private ControlGraphSeries aSeries = new ControlGraphSeries( "A", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, a );

    public static final int MAX_T = 500;

    private UpdateStrategy positionDriven = new PositionDriven();
    private UpdateStrategy velocityDriven = new VelocityDriven();
    private UpdateStrategy accelDriven = new AccelDriven();

    private UpdateStrategy updateStrategy = positionDriven;

    public void setPositionDriven() {
        setUpdateStrategy( positionDriven );
    }

    public void setPosition( double x ) {
        this.x.setValue( x );
    }

    public ITemporalVariable getXVariable() {
        return x;
    }

    public double getPosition() {
        return x.getValue();
    }

    public void setVelocityDriven() {
        setUpdateStrategy( velocityDriven );
    }

    public void setVelocity( double v ) {
        this.v.setValue( v );
    }

    public void startRecording() {
        getTimeSeriesModel().startRecording();
    }

    public static class PositionDriven implements UpdateStrategy {
        public void update( MotionBody motionBody, double dt, double time ) {
            System.out.println( "MovingManMotionModel$PositionDriven.update, time=" + time );
        }
    }

    public static class VelocityDriven implements UpdateStrategy {
        public void update( MotionBody motionBody, double dt, double time ) {
        }
    }

    public static class AccelDriven implements UpdateStrategy {
        public void update( MotionBody motionBody, double dt, double time ) {
        }
    }

    public MovingManMotionModel( ConstantDtClock clock ) {
        super( clock, new TimeSeriesFactory.Default() );
        setMaxAllowedRecordTime( MAX_T );
    }

    public ControlGraphSeries[] getControlGraphSeriesArray() {
        return new ControlGraphSeries[]{xSeries, vSeries, aSeries};
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

    public UpdateStrategy getPositionDriven() {
        return positionDriven;
    }

    public UpdateStrategy getVelocityDriven() {
        return velocityDriven;
    }

    public UpdateStrategy getAccelDriven() {
        return accelDriven;
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        updateStrategy.update( null, dt, super.getTime() );
    }
}
