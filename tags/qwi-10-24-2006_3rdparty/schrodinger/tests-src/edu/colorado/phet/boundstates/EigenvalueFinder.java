/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 9:46:53 AM
 * Copyright (c) Mar 8, 2006 by Sam Reid
 */

public class EigenvalueFinder {
    Schro eqn;
    private double min;
    private double max;
    private boolean killed = false;
    private double minValue;
    private double maxValue;
    private double hint = 0.0;

    public EigenvalueFinder( double min, double max ) {
        eqn = new Schro();
        this.min = min;
        this.max = max;
    }

//    public static double[] getEigenvalues( int num ) {
//        return new EigenvalueFinder( num ).getEigenvalues();
//    }

    private double getEigenvalue() {

        minValue = tryEnergy( min );
        maxValue = tryEnergy( max );

        while( !killed ) {
//            owner.energyChanged( getEstimate() );
            double estimate = getEstimate();
            improveEstimate();
            try {
                Thread.sleep( 5 );
            }
            catch( InterruptedException e ) {
                break;
            }
        }

        //System.err.println("...finished");
//        finished = true;
//        owner.searchFinished();
        return getEstimate();
    }

    /**
     * Returns the current &quot;best estimate&quot; of the solution;
     * that is, the midpoint of the bisection interval.
     */
    public double getEstimate() {
        return ( min + max ) / 2;
    }

    /**
     * Performs one step of the search.
     */
    void improveEstimate() {
        double energy = ( min + max ) / 2;

        //	Check for fp underflow
        if( energy == min || energy == max ) {
            killed = true;
        }

        double newValue = tryEnergy( energy );
        boolean low, high;


        high = ( maxValue > 0 && newValue < 0 ) || ( maxValue < 0 && newValue > 0 );
        low = ( minValue > 0 && newValue < 0 ) || ( minValue < 0 && newValue > 0 );


        if( high == low ) {
            high = hint > energy;
        }

        if( high ) {
            min = energy;
            minValue = newValue;
            return;
        }
        else {
            max = energy;
            maxValue = newValue;
            return;
        }

    }

    /**
     * Attempts to solve the equation with the specified energy, and returns
     * the value at the end of the <i>x</i> range.  For a solution to the
     * Schrodinger equation, this method should return (approximately) zero.
     */
    double tryEnergy( double energy ) {
        eqn.setEnergy( energy );
        int steps = 4000;
        double intRange = 10;
        double[] soln = new double[steps + 1];
        double stepSize = intRange / steps;
//        eqn.solve( -intRange / 2, stepSize, soln );
        return soln[steps] - soln[1];
    }
}
