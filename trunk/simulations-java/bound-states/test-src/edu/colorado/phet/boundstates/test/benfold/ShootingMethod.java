/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test.benfold;

/**
 * Thread to search an interval for a solution to a Schrodinger equation.
 */
public class ShootingMethod {
    /**
     * If neither half of the interval is guaranteed to contain a solution,
     * the search will move towards this value, which is initialised from the
     * original energy parameter of the equation.
     */
    protected double hint;
    protected double min, max, minValue, maxValue, intRange;
    protected int steps;
    protected boolean done;
    protected Schrodinger eqn;

    /**
     * Creates a new solution searcher
     *
     * @param eqn      The equation to solve
     * @param min      A lower bound for the solution
     * @param max      An upper bound for the solution
     * @param intRange The range over which the solution should be
     *                 evaluated
     * @param steps    The number of steps to be used when evaluating the
     *                 solution
     */
    public ShootingMethod( Schrodinger eqn, double min, double max, double intRange, int steps, double hint ) {
        this.eqn = eqn;
        this.min = min;
        this.max = max;
        this.intRange = intRange;
        this.steps = steps;
        this.hint = hint;
    }

    public void run() {
        minValue = tryEnergy( min );
        maxValue = tryEnergy( max );

        while( !done ) {
            improveEstimate();
        }
    }

    /**
     * Performs one step of the search.
     */
    protected void improveEstimate() {
        double energy = ( min + max ) / 2;

        //	Check for fp underflow
        if( energy == min || energy == max ) {
            done = true;
        }

        double newValue = tryEnergy( energy );

        boolean high = ( maxValue > 0 && newValue < 0 ) || ( maxValue < 0 && newValue > 0 );
        boolean low = ( minValue > 0 && newValue < 0 ) || ( minValue < 0 && newValue > 0 );

        if( high == low ) {
            high = hint > energy;
        }
        if( high ) {
            min = energy;
            minValue = newValue;
        }
        else {
            max = energy;
            maxValue = newValue;
        }
    }


    /**
     * Attempts to solve the equation with the specified energy, and returns
     * the value at the end of the <i>x</i> range.  For a solution to the
     * Schrodinger equation, this method should return (approximately) zero.
     */
    public double tryEnergy( double energy ) {
        eqn.setEnergy( energy );
        double[] soln = new double[steps + 1];
        eqn.solve( -intRange / 2, intRange / steps, soln );
        return soln[steps] - soln[1];
    }

    /**
     * Returns the current &quot;best estimate&quot; of the solution;
     * that is, the midpoint of the bisection interval.
     */
    public double getEstimate() {
        return ( min + max ) / 2;
    }

}
