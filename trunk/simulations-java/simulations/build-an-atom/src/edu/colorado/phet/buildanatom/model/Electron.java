/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.model;

/**
 * Class the represents an electron in the model.
 *
 * @author John Blanco
 */
public class Electron extends SubatomicParticle {
    public static final double RADIUS = 5;

    public Electron( double x, double y ) {
        super( RADIUS, x, y );
    }

    public Electron() {
        this( 0, 0 );
    }
}
