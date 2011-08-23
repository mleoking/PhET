// Copyright 2002-2011, University of Colorado
package org.reid.scenic.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
public class Atom {
    public final ImmutableVector2D position;
    public final ImmutableVector2D velocity;
    public final double mass;

    public Atom( ImmutableVector2D position, ImmutableVector2D velocity, double mass ) {
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
    }

    public Atom velocity( ImmutableVector2D velocity ) {
        return new Atom( position, velocity, mass );
    }
}