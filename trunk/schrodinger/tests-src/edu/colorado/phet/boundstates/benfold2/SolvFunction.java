/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.benfold2;

/**
 * Simple implementation of {@link edu.colorado.phet.boundstates.benfold2.Solvable Solvable} for a {@link edu.colorado.phet.boundstates.benfold2.Function
 * Function}.  This class is intended to be used for the plotting of simple
 * functions.
 */
class SolvFunction implements Solvable {
    /**
     * @param f The function to be used when producing results
     */
    public SolvFunction( Function f ) {
        this.function = f;
    }


    /**
     * Simply evaluates the function at each value of x0.
     */
    public void solve( double x0, double step, double[] vals ) {
        for( int i = 0; i < vals.length; i++ ) {
            vals[i] = function.evaluate( x0 );
            x0 += step;
        }
    }


    protected Function function;
}
