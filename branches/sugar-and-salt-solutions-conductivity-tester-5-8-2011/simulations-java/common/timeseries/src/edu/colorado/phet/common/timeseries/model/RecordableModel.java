// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.timeseries.model;

/**
 * Author: Sam Reid
 * May 15, 2007, 7:39:55 PM
 */
public interface RecordableModel {
    void stepInTime( double simulationTimeChange );

    Object getState();

    void setState( Object o );

    void resetTime();

    void clear();
}
