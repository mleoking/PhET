/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.math;

import java.util.ArrayList;

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
    private double t1;
    private double s;
    private double t2;

    private ArrayList transformListeners = new ArrayList();

    public interface Listener {
        void transformChanged( LinearTransform1d transform );
    }

    public void addListener( Listener listener ) {
        transformListeners.add( listener );
    }

    public LinearTransform1d( double minInput, double maxInput, double minOutput, double maxOutput ) {
        this.minInput = minInput;
        this.maxInput = maxInput;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;

        update();
    }

    private void update() {
        t1 = ( -minInput );
        double scale = ( maxOutput - minOutput ) / ( maxInput - minInput );
        s = scale;
        t2 = minOutput;
        for( int i = 0; i < transformListeners.size(); i++ ) {
            Listener listener = (Listener)transformListeners.get( i );
            listener.transformChanged( this );
        }
    }

    public double transform( double x ) {
        x = t1 + x;
        x = s * x;
        x = t2 + x;
        return x;
    }

    public void setMinInput( double minInput ) {
        this.minInput = minInput;
        update();
    }

    public void setMaxInput( double maxInput ) {
        this.maxInput = maxInput;
        update();
    }

    public void setMinOutput( double minOutput ) {
        this.minOutput = minOutput;
        update();
    }

    public void setMaxOutput( double maxOutput ) {
        this.maxOutput = maxOutput;
        update();
    }

    public void setInput( double min, double max ) {
        this.minInput = min;
        this.maxInput = max;
        update();
    }

    public void setOutput( double min, double max ) {
        this.minOutput = min;
        this.maxOutput = max;
        update();
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

    public LinearTransform1d getInvertedInstance() {
        return new LinearTransform1d( minOutput, maxOutput, minInput, maxInput );
    }

    public static void main( String[] args ) {
        LinearTransform1d map = new LinearTransform1d( -10, 10, 2, 3 );

        for( double d = map.getMinInput(); d <= map.getMaxInput(); d += .01 ) {
            double out = map.transform( d );
            System.out.println( "in = " + d + ", out=" + out );
        }
    }

    public double getInputRange() {
        return maxInput - minInput;
    }
}
