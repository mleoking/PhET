/** Sam Reid*/
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.plots.TimePoint;

/**
 * User: Sam Reid
 * Date: Oct 19, 2004
 * Time: 6:50:36 PM
 * Copyright (c) Oct 19, 2004 by Sam Reid
 */
public class MovingManModel {
    private MovingManTimeModel timeModel;
    public static final int numSmoothingPoints = 1;
    private int maxManPosition = 10;
    private Man man;
    private AbstractClock clock;
    private DataSuite positionDataSuite;
    private DataSuite velocityDataSuite;
    private DataSuite accelerationDataSuite;
    private int numSmoothingPosition;
    private int numVelocitySmoothPoints;
    private int numAccSmoothPoints;
    private MovingManModule movingManModule;
    private double minTime = 0;
    private double maxTime = 20;

    public MovingManModel( MovingManModule movingManModule, AbstractClock clock ) {
        timeModel = new MovingManTimeModel( movingManModule );
        this.movingManModule = movingManModule;
        this.clock = clock;
        numSmoothingPosition = numSmoothingPoints;
        numVelocitySmoothPoints = numSmoothingPoints;
        numAccSmoothPoints = numSmoothingPoints;

        positionDataSuite = new DataSuite( numSmoothingPosition );
        velocityDataSuite = new DataSuite( numVelocitySmoothPoints );
        accelerationDataSuite = new DataSuite( numAccSmoothPoints );

        positionDataSuite.setDerivative( velocityDataSuite );
        velocityDataSuite.setDerivative( accelerationDataSuite );
        man = new Man( 0, -maxManPosition, maxManPosition );
        man.addListener( new CollisionAudioEffects( movingManModule, man ) );
    }

    public void setNumSmoothingPoints( int n ) {
        positionDataSuite.setNumSmoothingPoints( n );
        velocityDataSuite.setNumSmoothingPoints( n );
        accelerationDataSuite.setNumSmoothingPoints( n );
    }

    public void reset() {
        getTimeModel().reset();
        man.reset();
        positionDataSuite.reset();
        velocityDataSuite.reset();
        accelerationDataSuite.reset();
    }

    public void step( double dt ) {
        positionDataSuite.addPoint( man.getPosition(), timeModel.getRecordTimer().getTime() );
        positionDataSuite.updateSmoothedSeries();
        positionDataSuite.updateDerivative( dt );
        velocityDataSuite.updateSmoothedSeries();
        velocityDataSuite.updateDerivative( dt );
        accelerationDataSuite.updateSmoothedSeries();
    }

    public void setReplayTimeIndex( int timeIndex ) {
        if( timeIndex < positionDataSuite.numSmoothedPoints() && timeIndex >= 0 ) {
            double x = positionDataSuite.smoothedPointAt( timeIndex );
            man.setPosition( x );
        }
    }

    public double getVelocityDataSuite() {
        if( velocityDataSuite == null ) {
            return 0;
        }
        if( velocityDataSuite.numSmoothedPoints() == 0 ) {
            return 0;
        }
        else {
            return velocityDataSuite.smoothedPointAt( velocityDataSuite.numSmoothedPoints() - 1 );
        }
    }

    public Man getMan() {
        return man;
    }

    public DataSuite getPositionDataSuite() {
        return positionDataSuite;
    }

    public DataSuite getVelocitySeries() {
        return velocityDataSuite;
    }

    public DataSuite getAccelerationDataSuite() {
        return accelerationDataSuite;
    }

    public double getNumSmoothingPosition() {
        return numSmoothingPosition;
    }

    public int getNumVelocitySmoothPoints() {
        return numVelocitySmoothPoints;
    }

    public double getMaxTime() {
//        return 5;
        return maxTime;
    }

    public double getMinTime() {
        return minTime;
    }

    public double getMaxManPosition() {
        return maxManPosition;
    }

    public ModelElement getMainModelElement() {
        return timeModel.getMainModelElement();
    }

    public void fireReset() {
        timeModel.fireReset();
    }

    public void setRecordMode() {
        timeModel.setRecordMode();
    }

    public int getNumSmoothingPoints() {
        return timeModel.getNumSmoothingPoints();
    }

    public MovingManTimeModel getTimeModel() {
        return timeModel;
    }

//        public void setReplayTimeIndex( int timeIndex ) {
//        if( timeIndex < positionDataSuite.numSmoothedPoints() && timeIndex >= 0 ) {
//            double x = positionDataSuite.smoothedPointAt( timeIndex );
//            man.setPosition( x );
//        }
//    }

    public void setReplayTime( double requestedTime ) {
        TimePoint timePoint = positionDataSuite.getSmoothedDataSeries().getValueForTime( requestedTime );
        man.setPosition( timePoint.getValue() );
//        double position=positionDataSuite.getSmoothedDataSeries().
//        man.setPosition( );
    }
}
