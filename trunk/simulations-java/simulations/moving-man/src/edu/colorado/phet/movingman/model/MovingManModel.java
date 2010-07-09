package edu.colorado.phet.movingman.model;

import bsh.EvalError;
import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.charts.ChartCursor;
import edu.colorado.phet.common.motion.charts.MutableBoolean;
import edu.colorado.phet.common.motion.charts.Range;
import edu.colorado.phet.common.motion.charts.TemporalDataSeries;
import edu.colorado.phet.common.motion.model.TimeData;

import java.util.ArrayList;

public class MovingManModel {
    public static final double FPS = 24.0;
    public static final int CLOCK_DELAY_MS = (int) (1.0 / FPS * 1000);
    public static final double DT = 1.0 / FPS;
    private MovingMan movingMan;

    public static final int DERIVATIVE_RADIUS = 1;//Kathy chose this value because it is a good balance between derivative sharpness and responsiveness
    public static final int NUMBER_MOUSE_POINTS_TO_AVERAGE = 4;//Kathy chose this value because it smoothes well enough, but without creating too much of a lag between the mouse and the character

    //These serieses are used for computing derivatives.  Must be size limited to avoid processor/memory leaks
    //These serieses do not all obtain data at the same times, derivatives are centered, so v and a values are obtained
    //after obtaining new x values.
    private static final int sizeLimit = Math.max(NUMBER_MOUSE_POINTS_TO_AVERAGE, (DERIVATIVE_RADIUS * 2 + 1) * 2);
    private TemporalDataSeries mouseDataModelSeries = new TemporalDataSeries.LimitedSize(sizeLimit);
    private TemporalDataSeries positionModelSeries = new TemporalDataSeries.LimitedSize(sizeLimit);
    private TemporalDataSeries velocityModelSeries = new TemporalDataSeries.LimitedSize(sizeLimit);
    private TemporalDataSeries accelerationModelSeries = new TemporalDataSeries.LimitedSize(sizeLimit);

    //These serieses are displayed in the graphs.
    private TemporalDataSeries positionGraphSeries = new TemporalDataSeries.LimitedTime(20.0);
    private TemporalDataSeries velocityGraphSeries = new TemporalDataSeries.LimitedTime(20.0);
    private TemporalDataSeries accelerationGraphSeries = new TemporalDataSeries.LimitedTime(20.0);

    private ChartCursor chartCursor = new ChartCursor();
    private double time = 0.0;
    private double mousePosition;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private boolean VELOCITY_VECTOR_VISIBLE_BY_DEFAULT = false;
    private boolean ACCELERATION_VECTOR_VISIBLE_BY_DEFAULT = false;
    private boolean WALLS_BY_DEFAULT = true;
    private MutableBoolean velocityVectorVisible = new MutableBoolean(VELOCITY_VECTOR_VISIBLE_BY_DEFAULT);
    private MutableBoolean accelerationVectorVisible = new MutableBoolean(ACCELERATION_VECTOR_VISIBLE_BY_DEFAULT);
    private MutableBoolean walls = new MutableBoolean(WALLS_BY_DEFAULT);
    protected final Range modelRange = new Range(-10, 10);
//    protected final Range modelRange = new Range(10, -10);
    private Range range = modelRange;
    private BooleanGetter isPaused;
    private MutableBoolean positionMode;
    private MutableBoolean accelerationMode;
    private ExpressionEvaluator expressionEvaluator;
    private ArrayList<EvalErrorListener> evalErrorListeners = new ArrayList<EvalErrorListener>();
    private MutableBoolean velocityMode;
    private MyObservableDouble timeProperty = new MyObservableDouble(0.0);

    public void historyRemainderCleared(double time) {
        mouseDataModelSeries.clearPointsAfter(time);
        positionModelSeries.clearPointsAfter(time);
        velocityModelSeries.clearPointsAfter(time);
        accelerationModelSeries.clearPointsAfter(time);

        positionGraphSeries.clearPointsAfter(time);
        velocityGraphSeries.clearPointsAfter(time);
        accelerationGraphSeries.clearPointsAfter(time);
    }

    public Range getModelRange() {
        return modelRange;
    }

    public MutableBoolean getPositionMode() {
        return positionMode;
    }

    public void setExpression(ExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
    }

    public MutableBoolean getAccelerationMode() {
        return accelerationMode;
    }

    public MutableBoolean getVelocityMode() {
        return velocityMode;
    }

    public ObservableDouble getTimeProperty() {
        return timeProperty;
    }

    public static interface BooleanGetter {
        boolean isTrue();
    }

    public MovingManModel(BooleanGetter isPaused) {//need to be able to update position immediately when paused instead of smoothing through the mouse data series
        this.movingMan = new MovingMan();
        this.isPaused = isPaused;

        //Provide mutable properties so that clients can subscribe for mode changes easily
        positionMode = new MutableBoolean(false);
        {
            final MovingMan.Listener positonModeListener = new MovingMan.Listener() {
                public void changed() {
                    positionMode.setValue(getMovingMan().getMotionStrategy() == MovingMan.POSITION_DRIVEN);
                }
            };
            getMovingMan().addListener(positonModeListener);
            positonModeListener.changed();
        }

        velocityMode = new MutableBoolean(false);
        {
            final MovingMan.Listener velocityModeListener = new MovingMan.Listener() {
                public void changed() {
                    velocityMode.setValue(getMovingMan().getMotionStrategy() == MovingMan.VELOCITY_DRIVEN);
                }
            };
            getMovingMan().addListener(velocityModeListener);
            velocityModeListener.changed();
        }

        accelerationMode = new MutableBoolean(false);
        {
            final MovingMan.Listener accelerationModeListener = new MovingMan.Listener() {
                public void changed() {
                    accelerationMode.setValue(getMovingMan().getMotionStrategy() == MovingMan.ACCELERATION_DRIVEN);
                }
            };
            accelerationModeListener.changed();
            getMovingMan().addListener(accelerationModeListener);
        }
    }

    public void resetAll() {
        movingMan.resetAll();
        walls.setValue(WALLS_BY_DEFAULT);
        velocityVectorVisible.setValue(VELOCITY_VECTOR_VISIBLE_BY_DEFAULT);
        accelerationVectorVisible.setValue(ACCELERATION_VECTOR_VISIBLE_BY_DEFAULT);

        clear();
    }

    public static class WallResult {
        public final double position;
        public final boolean collided;

        public WallResult(double position, boolean collided) {
            this.position = position;
            this.collided = collided;
        }
    }

    public WallResult clampIfWalled(double x) {
        double clamped = range.clamp(x);
        if (walls.getValue()) return new WallResult(clamped, clamped != x);
        else return new WallResult(x, false);
    }

    public void simulationTimeChanged(double dt) {
        time = time + dt;
        updateTimeProperty();
        if (movingMan.isPositionDriven()) {
            double previousPosition = movingMan.getPosition();
            double averagePosition;
            if (expressionEvaluator == null) {//Average samples from the mouse
                mouseDataModelSeries.addPoint(clampIfWalled(mousePosition).position, time);
                //take the position as the average of the latest mouseDataSeries points.
                TimeData[] position = mouseDataModelSeries.getPointsInRange(mouseDataModelSeries.getNumPoints() - NUMBER_MOUSE_POINTS_TO_AVERAGE, mouseDataModelSeries.getNumPoints());
                double sum = 0;
                for (TimeData timeData : position) {
                    sum += timeData.getValue();
                }
                averagePosition = clampIfWalled(sum / position.length).position;
                positionModelSeries.addPoint(averagePosition, time);
            } else {//use expression evaluator
                double v = 0;
                try {
                    v = expressionEvaluator.evaluate(time);
                } catch (EvalError evalError) {
//                    evalError.printStackTrace();
                    for (EvalErrorListener evalErrorListener : evalErrorListeners)
                        evalErrorListener.errorOccurred(evalError);
                }
                averagePosition = clampIfWalled(v).position;
                setMousePosition(averagePosition);
                mouseDataModelSeries.addPoint(averagePosition, time);
                positionModelSeries.addPoint(averagePosition, time);
            }

            //update model derivatives
            velocityModelSeries.setData(estimateCenteredDerivatives(positionModelSeries));
            accelerationModelSeries.setData(estimateCenteredDerivatives(velocityModelSeries));

            positionGraphSeries.addPoint(averagePosition, time);
            velocityGraphSeries.addPoint(getPointAtTime(velocityModelSeries, time));
            accelerationGraphSeries.addPoint(getPointAtTime(accelerationModelSeries, time));

            //no integrals

            //set instantaneous values
            movingMan.setPosition(averagePosition);
            double instantVelocity = velocityGraphSeries.getLastPoint().getValue();
            if (Math.abs(instantVelocity) < 1E-6)
                instantVelocity = 0.0;//added a prevent high frequency wiggling around +/- 1E-12
            movingMan.setVelocity(instantVelocity);//TODO: subtract off derivative radius so that the last value showed on chart is the same as the value on the man

            double instantAcceleration = accelerationGraphSeries.getLastPoint().getValue();
            if (Math.abs(instantAcceleration) < 1E-6)
                instantAcceleration = 0.0;//prevent high frequency wiggling around +/- 1E-12
            movingMan.setAcceleration(instantAcceleration); //- DERIVATIVE_RADIUS * 2

            if (!hitsWall(previousPosition) && hitsWall(movingMan.getPosition())) {
                notifyCollided();
            }
        } else if (movingMan.isVelocityDriven()) {
            mouseDataModelSeries.clear();//so that if the user switches to mouse-driven, it won't remember the wrong location.
            //record set point
            velocityModelSeries.addPoint(movingMan.getVelocity(), time);
            velocityGraphSeries.addPoint(movingMan.getVelocity(), time);

            //update derivatives
            accelerationModelSeries.setData(estimateCenteredDerivatives(velocityModelSeries));
            accelerationGraphSeries.addPoint(accelerationModelSeries.getMidPoint());

            //update integrals
            final double targetPosition = movingMan.getPosition() + movingMan.getVelocity() * dt;
            WallResult wallResult = clampIfWalled(targetPosition);
            positionModelSeries.addPoint(wallResult.position, time);
            positionGraphSeries.addPoint(wallResult.position, time);

            //set instantaneous values
            setMousePosition(wallResult.position);//so that if the user switches to mouse-driven, it will have the right location
            movingMan.setPosition(wallResult.position);
            double instantAcceleration = accelerationGraphSeries.getLastPoint().getValue();
            if (Math.abs(instantAcceleration) < 1E-6)
                instantAcceleration = 0.0;//workaround to prevent high frequency wiggling around +/- 1E-12
            movingMan.setAcceleration(instantAcceleration);//todo: subtract - DERIVATIVE_RADIUS if possible
            if (wallResult.collided) {
                movingMan.setVelocity(0.0);
                notifyCollided();
            }
        } else if (movingMan.isAccelerationDriven()) {
            mouseDataModelSeries.clear();//so that if the user switches to mouse-driven, it won't remember the wrong location.
            double newVelocity = movingMan.getVelocity() + movingMan.getAcceleration() * dt;
            double estVel = (movingMan.getVelocity() + newVelocity) / 2.0;//todo: just use newVelocity?
            WallResult wallResult = clampIfWalled(movingMan.getPosition() + estVel * dt);

            //This ensures that there is a deceleration spike when crashing into a wall.  Without this code,
            //the acceleration remains at the user specified value or falls to 0.0, but it is essential to
            //show that crashing into a wall entails a suddent deceleration.
            if (wallResult.collided) {
                movingMan.setVelocityDriven();
                movingMan.setVelocity(newVelocity);
                time = time - dt;//roll back errant update
                simulationTimeChanged(dt); //move forward in velocity mode, since it is constrained
                return;
            }

            //record set point
            accelerationModelSeries.addPoint(movingMan.getAcceleration(), time);
            accelerationGraphSeries.addPoint(movingMan.getAcceleration(), time);

            //no derivatives

            //update integrals
            velocityGraphSeries.addPoint(newVelocity, time);
            velocityModelSeries.addPoint(newVelocity, time);

            positionGraphSeries.addPoint(wallResult.position, time);
            positionModelSeries.addPoint(wallResult.position, time);

            //set instantaneous values
            setMousePosition(wallResult.position);//so that if the user switches to mouse-driven, it will have the right location
            movingMan.setPosition(wallResult.position);
            movingMan.setVelocity(newVelocity);
            if (wallResult.collided) {
                movingMan.setVelocity(0.0);
                movingMan.setAcceleration(0.0);//todo: should have brief burst of acceleration against the wall in a collision.
            }
        }
    }

    private boolean hitsWall(double x) {
        return range.getMax() == x || range.getMin() == x;
    }

    private ArrayList<JListener> collisionListeners = new ArrayList<JListener>();

    public void addCollisionListener(JListener listener) {
        collisionListeners.add(listener);
    }

    private void notifyCollided() {
        for (JListener collisionListener : collisionListeners) {
            collisionListener.eventOccurred();
        }
    }

    //To get the serieses to match up, look up the value at the specified time in the derivative model
    //Note, if interpolation is added for derivatives, a better lookup algorithm will be needed

    private TimeData getPointAtTime(TemporalDataSeries series, double time) {
        for (int i = 0; i < series.getNumPoints(); i++) {
            if (series.getDataPoint(i).getTime() == time) {
                return series.getDataPoint(i);
            }
        }
        throw new RuntimeException("Couldn't find exact match");
    }

    private TimeData[] estimateCenteredDerivatives(TemporalDataSeries series) {
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

    public TemporalDataSeries getPositionGraphSeries() {
        return positionGraphSeries;
    }

    public TemporalDataSeries getVelocityGraphSeries() {
        return velocityGraphSeries;
    }

    public TemporalDataSeries getAccelerationGraphSeries() {
        return accelerationGraphSeries;
    }

    public ChartCursor getChartCursor() {
        return chartCursor;
    }

    public void clear() {
        time = 0.0;
        updateTimeProperty();
        setMousePosition(movingMan.getPosition());

        mouseDataModelSeries.clear();
        positionModelSeries.clear();
        velocityModelSeries.clear();
        accelerationModelSeries.clear();

        positionGraphSeries.clear();
        velocityGraphSeries.clear();
        accelerationGraphSeries.clear();
    }

    /**
     * Construct a recorded state from the values shown on the charts, which should be consistent.
     * Note that this does not record the current time (because we don't yet have good estimates for the derivatives).
     *
     * @return
     */
    public MovingManState getRecordingState() {
        return new MovingManState(positionGraphSeries.getLastPoint().getTime(), new ManState(positionGraphSeries.getLastPoint().getValue(),
                velocityGraphSeries.getLastPoint().getValue(), accelerationGraphSeries.getLastPoint().getValue(), movingMan.getMotionStrategy()), walls.getValue());
    }

    public void setPlaybackState(MovingManState state) {
        this.time = state.getTime();//recording and playing back the state time ensures that the user cannot put the cursor in a time between two samples, thus we don't need to interpolate
        this.walls.setValue(state.getWalls());
        this.movingMan.setState(state.getMovingManState());
        setMousePosition(state.getMovingManState().getPosition());
        this.chartCursor.setTime(time);
        updateTimeProperty();
    }

    private void updateTimeProperty() {
        timeProperty.setValue(time);
    }

    /**
     * This method allows recording the mouse position separately, so that it can be smoothed out before stored as data on the man character.
     *
     * @param mousePosition
     */
    public void setMousePosition(double mousePosition) {
        if (this.mousePosition != mousePosition) {
            this.mousePosition = clampIfWalled(mousePosition).position;
            if (isPaused.isTrue()) {
                movingMan.setPosition(mousePosition);
            }
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

    /**
     * Signify an error when beanshell evaluation fails.
     */
    public static interface EvalErrorListener {
        void errorOccurred(EvalError evalError);
    }

    private class MyObservableDouble extends ObservableDouble {
        public MyObservableDouble(double v) {
            super(v);
        }

        @Override
        public void setValue(double value) {
            super.setValue(value);
        }
    }
}
