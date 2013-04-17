// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.phetcommon.math.Function;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 2:49:12 AM
 */
public class CoordinateFrame {
    double min;
    double max;

    public CoordinateFrame( double min, double max ) {
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getRange() {
        return max - min;
    }

    public boolean contains( double value ) {
        return value >= min && value <= max;
    }

    public String toString() {
        return "min=" + min + ", max=" + max;
    }

    public double transform( double value, CoordinateFrame dstFrame ) {
        if( this.contains( value ) ) {
            return new Function.LinearFunction( getMin(), getMax(), dstFrame.getMin(), dstFrame.getMax() ).evaluate( value );
        }
        else {
            throw new RuntimeException( "Model frame doesn't contain value: " + value + ", sourceFrame=" + this );
        }
    }
}
