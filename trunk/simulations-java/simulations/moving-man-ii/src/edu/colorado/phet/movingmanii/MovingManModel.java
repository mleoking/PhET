package edu.colorado.phet.movingmanii;

import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.model.TimeData;

import java.util.ArrayList;

public class MovingManModel {
    private MovingMan movingMan;
    private MovingManDataSeries positionSeries = new MovingManDataSeries();
    private MovingManDataSeries velocitySeries = new MovingManDataSeries();
    private MovingManDataSeries accelerationSeries = new MovingManDataSeries();
    private ChartCursor chartCursor = new ChartCursor();
    private double time = 0.0;

    public MovingManModel() {
        this.movingMan = new MovingMan();
    }

    public void simulationTimeChanged(double dt) {
        time = time + dt;
        if (movingMan.isPositionDriven()) {
            //record set point
            positionSeries.addPoint(movingMan.getPosition(), time);

            //update derivatives
            velocitySeries.setData(estimateCenteredDerivatives(positionSeries));
            accelerationSeries.setData(estimateCenteredDerivatives(velocitySeries));

            //no integrals

            //set instantaneous values
            movingMan.setVelocity(velocitySeries.getDataPoint(velocitySeries.getNumPoints() - 1).getValue());
            movingMan.setAcceleration(accelerationSeries.getDataPoint(accelerationSeries.getNumPoints() - 1).getValue());
        } else if (movingMan.isVelocityDriven()) {
            //record set point
            velocitySeries.addPoint(movingMan.getVelocity(), time);

            //update derivatives
            accelerationSeries.setData(estimateCenteredDerivatives(velocitySeries));

            //update integrals
            double newPosition = movingMan.getPosition() + movingMan.getVelocity() * dt;
            positionSeries.addPoint(newPosition, time);

            //set instantaneous values
            movingMan.setPosition(newPosition);
            movingMan.setAcceleration(accelerationSeries.getDataPoint(accelerationSeries.getNumPoints() - 1).getValue());
        } else if (movingMan.isAccelerationDriven()) {
            //record set point
            accelerationSeries.addPoint(movingMan.getAcceleration(), time);

            //no derivatives

            //update integrals
            double newVelocity = movingMan.getVelocity() + movingMan.getAcceleration() * dt;
            velocitySeries.addPoint(newVelocity, time);

            double estVel = (movingMan.getVelocity() + newVelocity) / 2.0;//todo: just use newVelocity?
            double newPosition = movingMan.getPosition() + estVel * dt;
            positionSeries.addPoint(newPosition, time);

            //set instantaneous values
            movingMan.setPosition(newPosition);
            movingMan.setVelocity(newVelocity);
        }
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

    public ChartCursor getChartCursor() {
        return chartCursor;
    }

    public void clear() {
        time = 0.0;
        positionSeries.clear();
        velocitySeries.clear();
        accelerationSeries.clear();
    }

    public MovingManState getRecordingState() {
        return new MovingManState(time, movingMan.getState());
    }

    public void setPlaybackState(MovingManState state) {
        this.time = state.getTime();
        this.movingMan.setState(state.getMovingManState());
        this.chartCursor.setTime(time);
        //todo: notify time changed?
    }
}
