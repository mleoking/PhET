/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.model;

/**
 * Class that represents a proton in the model.
 *
 * @author John Blanco
 */
public class Proton extends SubatomicParticle {
    public static final double RADIUS = 8;

    public Proton( double x, double y ) {
        super( RADIUS, x, y );
    }

    public Proton() {
        this( 0, 0 );
    }
}
