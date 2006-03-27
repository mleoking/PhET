/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test.benfold;

/**
 * Represents an equation (or some more abstract object) which can be solved
 * or sampled for a set of evenly spaced x-values.  This may involve
 * function evaluation, or approximate integration, or some other technique.
 */
public interface Solvable {
    /**
     * Requests that the supplied array be filled with solution data.  The
     * <i>n</i>th element of the array should be the solution at <i>x</i> =
     * x0 + step*<i>n</i>.
     *
     * @param x0   The initial x-value
     * @param step The increment to be applied to x0
     * @param vals The array which will hold the solution data
     */
    public void solve( double x0, double step, double[] vals );
}
