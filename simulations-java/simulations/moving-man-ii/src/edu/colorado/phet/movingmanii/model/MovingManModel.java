package edu.colorado.phet.movingmanii.model;

import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.movingmanii.charts.ChartCursor;
import edu.colorado.phet.movingmanii.view.Range;

import java.util.ArrayList;

public class MovingManModel {
    private MovingMan movingMan;
    private MovingManDataSeries mouseDataSeries = new MovingManDataSeries();
    private MovingManDataSeries positionSeries = new MovingManDataSeries();
    private MovingManDataSeries velocitySeries = new MovingManDataSeries();
    private MovingManDataSeries accelerationSeries = new MovingManDataSeries();
    private ChartCursor chartCursor = new ChartCursor();
    private double time = 0.0;
    private double mousePosition;
    public static final int DERIVATIVE_RADIUS = 1;//Kathy chose this value because it is a good balance betweenw
    private static final int NUMBER_MOUSE_POINTS_TO_AVERAGE = 4;//Kathy chose this value because it smoothes well enough, but without creating too much of a lag between the mouse and the character
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private MutableBoolean velocityVectorVisible = new MutableBoolean(false);
    private MutableBoolean accelerationVectorVisible = new MutableBoolean(false);
    private MutableBoolean walls = new MutableBoolean(true);
    private Range range = new Range(-10, 10);

    public MovingManModel() {
        this.movingMan = new MovingMan();
    }

    public void simulationTimeChanged(double dt) {
        time = time + dt;
        if (movingMan.isPositionDriven()) {
            mouseDataSeries.addPoint(mousePosition, time);
//            //take the position as the average of the latest mouseDataSeries points.
            TimeData[] position = mouseDataSeries.getPointsInRange(mouseDataSeries.getNumPoints() - NUMBER_MOUSE_POINTS_TO_AVERAGE, mouseDataSeries.getNumPoints());
            double sum = 0;
            for (TimeData timeData : position) {
                sum += timeData.getValue();
            }
            double averagePosition = sum / position.length;

            //record set point based on derivatives
            positionSeries.addPoint(averagePosition, time);

            //update derivatives
            velocitySeries.setData(estimateCenteredDerivatives(positionSeries));
            accelerationSeries.setData(estimateCenteredDerivatives(velocitySeries));

            //no integrals

            //set instantaneous values
            movingMan.setPosition(averagePosition);
            movingMan.setVelocity(velocitySeries.getDataPoint(velocitySeries.getNumPoints() - 1).getValue());//TODO: subtract off derivative radius so that the last value showed on chart is the same as the value on the man
            movingMan.setAcceleration(accelerationSeries.getDataPoint(accelerationSeries.getNumPoints() - 1).getValue()); //- DERIVATIVE_RADIUS * 2
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
            movingMan.setAcceleration(accelerationSeries.getDataPoint(accelerationSeries.getNumPoints() - 1).getValue());//todo: subtract - DERIVATIVE_RADIUS if possible
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
        int radius = DERIVATIVE_RADIUS;
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
        return new MovingManState(time, movingMan.getState(), walls.getValue());
    }

    public void setPlaybackState(MovingManState state) {
        this.time = state.getTime();
        this.walls.setValue(state.getWalls());
        this.movingMan.setState(state.getMovingManState());
        this.chartCursor.setTime(time);
        //todo: notify time changed?
    }

    /**
     * This method allows recording the mouse position separately, so that it can be smoothed out before stored as data on the man character.
     *
     * @param mousePosition
     */
    public void setMousePosition(double mousePosition) {
        if (this.mousePosition != mousePosition) {
            this.mousePosition = mousePosition;
            for (Listener listener : listeners) {
                listener.mousePositionChanged();
            }
        }
    }

    public double getMousePosition() {
        return mousePosition;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public Range getRange() {
        return range;
    }

    public MutableBoolean getWalls() {
        return walls;
    }

    public static interface Listener {
        void mousePositionChanged();
    }

    public MutableBoolean getVelocityVectorVisible() {
        return velocityVectorVisible;
    }

    public MutableBoolean getAccelerationVectorVisible() {
        return accelerationVectorVisible;
    }
}
