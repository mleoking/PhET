// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

/**
 * Context for being able to reset the "Fraction Lab" tab,
 * which is done by creating a new canvas and setting it as the simulation panel.
 *
 * @author Sam Reid
 */
public interface FractionLabCanvasContext {
    void resetCanvas();
}