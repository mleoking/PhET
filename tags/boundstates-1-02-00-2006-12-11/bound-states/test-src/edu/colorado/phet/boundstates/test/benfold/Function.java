/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test.benfold;

/**
 * This interface should be implemented by all classes that can behave as
 * a function.  A function may be plotted directly using the SolvFunction class.
 */
public interface Function {
    public double evaluate( double x );
}
