/** Sam Reid*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.model.clock.AbstractClock;

/**
 * User: Sam Reid
 * Date: Oct 19, 2004
 * Time: 6:50:36 PM
 * Copyright (c) Oct 19, 2004 by Sam Reid
 */
public class MovingManModel {
    public static final int numSmoothingPoints = 10;
    private int maxManPosition = 10;
    public static final double TIMER_SCALE = 1.0 / 50;

    private Man man;
    private AbstractClock clock;
    private SmoothDataSeries position;
    private SmoothDataSeries velocity;
    private SmoothDataSeries acceleration;
    private int numSmoothingPosition;
    private int numVelocitySmoothPoints;
    private int numAccSmoothPoints;
    private MovingManModule movingManModule;
    private double minTime = 0;
    private double maxTime = 20;

    public MovingManModel( MovingManModule movingManModule, AbstractClock clock ) {
        this.movingManModule = movingManModule;
        this.clock = clock;
        numSmoothingPosition = numSmoothingPoints;
        numVelocitySmoothPoints = numSmoothingPoints;
        numAccSmoothPoints = numSmoothingPoints;

        position = new SmoothDataSeries( numSmoothingPosition );
        velocity = new SmoothDataSeries( numVelocitySmoothPoints );
        acceleration = new SmoothDataSeries( numAccSmoothPoints );

        position.setDerivative( velocity );
        velocity.setDerivative( acceleration );
        man = new Man( 0, -maxManPosition, maxManPosition );
    }

    public void setNumSmoothingPoints( int n ) {
        position.setNumSmoothingPoints( n );
        velocity.setNumSmoothingPoints( n );
        acceleration.setNumSmoothingPoints( n );
    }

    public void reset() {
        man.reset();
        position.reset();
        velocity.reset();
        acceleration.reset();
    }

    public void step( double dt ) {
        position.addPoint( man.getX() );
        position.updateSmoothedSeries();
        position.updateDerivative( dt * TIMER_SCALE );
        velocity.updateSmoothedSeries();
        velocity.updateDerivative( dt * TIMER_SCALE );
        acceleration.updateSmoothedSeries();
    }

    public void setReplayTimeIndex( int timeIndex ) {
        if( timeIndex < position.numSmoothedPoints() && timeIndex >= 0 ) {
            double x = position.smoothedPointAt( timeIndex );
            man.setX( x );
        }
    }

    public double getVelocity() {
        if( velocity == null ) {
            return 0;
        }
        if( velocity.numSmoothedPoints() == 0 ) {
            return 0;
        }
        else {
            return velocity.smoothedPointAt( velocity.numSmoothedPoints() - 1 );
        }
    }

    public double getFinalManPosition() {
        if( position.getData().size() > 0 ) {
            return position.getData().getLastPoint();
        }
        else {
            return man.getX();
        }
    }

    public Man getMan() {
        return man;
    }

    public SmoothDataSeries getPosition() {
        return position;
    }

    public SmoothDataSeries getVelocitySeries() {
        return velocity;
    }

    public SmoothDataSeries getAcceleration() {
        return acceleration;
    }

    public double getNumSmoothingPosition() {
        return numSmoothingPosition;
    }

    public int getNumVelocitySmoothPoints() {
        return numVelocitySmoothPoints;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public double getMinTime() {
        return minTime;
    }

    public double getMaxManPosition() {
        return maxManPosition;
    }
}
