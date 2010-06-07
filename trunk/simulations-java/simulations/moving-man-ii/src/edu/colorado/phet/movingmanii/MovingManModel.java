package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jun 7, 2010
 * Time: 11:21:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class MovingManModel {
    private MovingMan movingMan;
    private MovingManDataSeries positionSeries = new MovingManDataSeries();
    private MovingManDataSeries velocitySeries = new MovingManDataSeries();
    private MovingManDataSeries accelerationSeries = new MovingManDataSeries();

    public MovingManModel(IClock clock) {
        this.movingMan = new MovingMan();
        clock.addClockListener(new ClockAdapter() {
            public void simulationTimeChanged(ClockEvent clockEvent) {
                positionSeries.addPoint(clockEvent.getSimulationTime(), movingMan.getPosition());
                velocitySeries.addPoint(clockEvent.getSimulationTime(), estimateVelocity());
                accelerationSeries.addPoint(clockEvent.getSimulationTime(), estimateAcceleration());
            }
        });
    }

    private double estimateAcceleration() {
        ArrayList<TimeData> timeData = new ArrayList<TimeData>();
        for (int i = velocitySeries.getNumPoints() - 10; i >= 0 && i < velocitySeries.getNumPoints(); i++) {
            Point2D pt = velocitySeries.getDataPoint(i);
            timeData.add(new TimeData(pt.getY(), pt.getX()));
        }
        double velocity = MotionMath.estimateDerivative(timeData.toArray(new TimeData[0]));
        return velocity;
    }

    private double estimateVelocity() {
        ArrayList<TimeData> timeData = new ArrayList<TimeData>();
        for (int i = positionSeries.getNumPoints() - 10; i >= 0 && i < positionSeries.getNumPoints(); i++) {
            Point2D pt = positionSeries.getDataPoint(i);
            timeData.add(new TimeData(pt.getY(), pt.getX()));
        }
        double velocity = MotionMath.estimateDerivative(timeData.toArray(new TimeData[0]));
        return velocity;
    }

    public MovingMan getMovingMan() {
        return movingMan;
    }

    public MovingManDataSeries getPositionSeries() {
        return positionSeries;
    }

    public MovingManDataSeries getVelocitySeries() {
        return velocitySeries;
    }

    public MovingManDataSeries getAccelerationSeries() {
        return accelerationSeries;
    }
}
