package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

import java.util.ArrayList;

public class MovingManModel {
    private MovingMan movingMan;
    private MovingManDataSeries positionSeries = new MovingManDataSeries();
    private MovingManDataSeries velocitySeries = new MovingManDataSeries();
    private MovingManDataSeries accelerationSeries = new MovingManDataSeries();

    public MovingManModel(IClock clock) {
        this.movingMan = new MovingMan();
        clock.addClockListener(new ClockAdapter() {
            public void simulationTimeChanged(ClockEvent clockEvent) {
                positionSeries.addPoint(movingMan.getPosition(), clockEvent.getSimulationTime());
                updateDerivatives();
            }
        });
    }

    private void updateDerivatives() {
        velocitySeries.setData(estimateCenteredDerivatives(positionSeries));
        accelerationSeries.setData(estimateCenteredDerivatives(velocitySeries));
    }

    private TimeData[] estimateCenteredDerivatives(MovingManDataSeries series) {
        int radius = 12;
        ArrayList<TimeData> points = new ArrayList<TimeData>();
        for (int i = 0; i < series.getNumPoints(); i++) {
            TimeData[] range = series.getPointsInRange(i - radius, i + radius);
            double derivative = MotionMath.estimateDerivative(range);
            points.add(new TimeData(derivative, series.getDataPoint(i).getTime()));
        }
        return points.toArray(new TimeData[points.size()]);
    }
//
//    private double estimateAcceleration() {
//        ArrayList<TimeData> timeData = new ArrayList<TimeData>();
//        for (int i = velocitySeries.getNumPoints() - 10; i >= 0 && i < velocitySeries.getNumPoints(); i++) {
//            Point2D pt = velocitySeries.getDataPoint(i);
//            timeData.add(new TimeData(pt.getY(), pt.getX()));
//        }
//        double velocity = MotionMath.estimateDerivative(timeData.toArray(new TimeData[0]));
//        return velocity;
//    }
//
//    private double estimateVelocity() {
//        ArrayList<TimeData> timeData = new ArrayList<TimeData>();
//        for (int i = positionSeries.getNumPoints() - 10; i >= 0 && i < positionSeries.getNumPoints(); i++) {
//            Point2D pt = positionSeries.getDataPoint(i);
//            timeData.add(new TimeData(pt.getY(), pt.getX()));
//        }
//        double velocity = MotionMath.estimateDerivative(timeData.toArray(new TimeData[0]));
//        return velocity;
//    }

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
