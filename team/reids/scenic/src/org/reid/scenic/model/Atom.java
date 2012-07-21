// Copyright 2002-2012, University of Colorado
package org.reid.scenic.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * @author Sam Reid
 */
public class Atom {
    public final Vector2D position;
    public final Vector2D velocity;
    public final double mass;

    public Atom( Vector2D position, Vector2D velocity, double mass ) {
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
    }

    public Atom velocity( Vector2D velocity ) {
        return new Atom( position, velocity, mass );
    }
}