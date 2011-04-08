// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

/**
 * Immutable data point class used in the intensity meter charts.
 *
 * @author Sam Reid
 */
public class DataPoint {
    public final double time;
    public final double value;

    public DataPoint( double time, double value ) {
        this.time = time;
        this.value = value;
    }
}
