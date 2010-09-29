/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.model;

/**
 * Class that represents a neutron in the model.
 *
 * @author John Blanco
 */
public class Neutron extends SubatomicParticle {
    public static final double RADIUS = 8;

    public Neutron( double x, double y ) {
        super( RADIUS, x, y );
    }

    public Neutron() {
        this( 0, 0 );
    }
}
