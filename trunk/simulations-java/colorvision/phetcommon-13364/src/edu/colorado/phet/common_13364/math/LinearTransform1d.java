/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common_13364.math;

/**
 * Performs a linear transform, given input range and output range.
 * 
 * @author Sam Reid
 * @version $Revision$
 */
public class LinearTransform1d {
    double minInput;
    double maxInput;
    double minOutput;
    double maxOutput;

    public LinearTransform1d( double minInput, double maxInput, double minOutput, double maxOutput ) {
        this.minInput = minInput;
        this.maxInput = maxInput;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
    }

    public double operate( double input ) {
        //could do bounds checking.
        double slope = ( maxOutput - minOutput ) / ( maxInput - minInput );
        double output = ( input - minInput ) * slope + minOutput;
        return output;
    }

    public double getMinInput() {
        return minInput;
    }

    public double getMaxInput() {
        return maxInput;
    }

    public double getMinOutput() {
        return minOutput;
    }

    public double getMaxOutput() {
        return maxOutput;
    }

    public static void main( String[] args ) {
        LinearTransform1d map = new LinearTransform1d( -10, 10, 2, 3 );

        for( double d = map.getMinInput(); d <= map.getMaxInput(); d += .01 ) {
            double out = map.operate( d );
            System.out.println( "in = " + d + ", out=" + out );
        }
    }
}
