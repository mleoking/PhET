package edu.colorado.phet.rotation.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:39:00 PM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */

public class RotationModel {
    ArrayList rotationModelStates = new ArrayList();
    UpdateStrategy updateStrategy = new PositionDriven();

    public RotationModel() {
        rotationModelStates.add( new RotationModelState() );
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
    }

    public RotationModelState getLastState() {
        return (RotationModelState)rotationModelStates.get( rotationModelStates.size() - 1 );
    }

    public void stepInTime( double dt ) {
        RotationModelState state = updateStrategy.update( this, dt );
        rotationModelStates.add( state );
    }

    public RotationModelState getStateFromEnd( int i ) {
        return getState( rotationModelStates.size() - 1 - i );
    }

    private RotationModelState getState( int index ) {
        return (RotationModelState)rotationModelStates.get( index );
    }

    public TimeData[] getAvailableAccelerationTimeSeries( int numPts ) {
        return getAccelerationTimeSeries( Math.min( numPts, getStateCount() ) );
    }

    /**
     * Returns values from the last numPts points of the acceleration time series.
     *
     * @param numPts the number of (most recent) points to get
     * @return the time series points.
     */
    public TimeData[] getAccelerationTimeSeries( int numPts ) {
        TimeData[] td = new TimeData[numPts];
        for( int i = 0; i < td.length; i++ ) {
            RotationModelState state = getState( getStateCount() - numPts + i );
            td[i] = new TimeData( state.getAngularAcceleration(), state.getTime() );
        }
        return td;
    }

    private int getStateCount() {
        return rotationModelStates.size();
    }
}
