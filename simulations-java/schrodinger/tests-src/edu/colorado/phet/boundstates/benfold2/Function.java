/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.benfold2;

/**
 * This interface should be implemented by all classes that can behave as
 * a function.  A function may be plotted directly using the {@link
 * SolvFunction SolvFunction} class.
 */
public interface Function {
    public double evaluate( double x );
}
