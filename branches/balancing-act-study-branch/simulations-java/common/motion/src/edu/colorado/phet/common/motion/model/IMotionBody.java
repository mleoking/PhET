// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.model;

/**
 * Created by: Sam
 * Dec 5, 2007 at 10:24:20 AM
 */
public interface IMotionBody {
    double getVelocity();

    double getAcceleration();

    double getPosition();

    void addAccelerationData( double acceleration, double time );

    void addVelocityData( double v, double time );

    void addPositionData( double v, double time );

    void addPositionData( TimeData data );

    void addVelocityData( TimeData data );

    void addAccelerationData( TimeData data );

    int getAccelerationSampleCount();

    TimeData[] getRecentVelocityTimeSeries( int i );

    int getPositionSampleCount();

    int getVelocitySampleCount();

    TimeData[] getRecentPositionTimeSeries( int i );

    void setAcceleration( double value );

    void setPositionDriven();
}
