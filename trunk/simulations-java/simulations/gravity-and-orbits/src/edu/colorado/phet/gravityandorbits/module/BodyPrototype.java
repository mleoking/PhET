// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
public class BodyPrototype {
    public final double radius;
    public final double mass;
    public final ImmutableVector2D position;
    public final ImmutableVector2D velocity;

    public BodyPrototype( double radius, double mass, ImmutableVector2D position, ImmutableVector2D velocity ) {
        this.radius = radius;
        this.mass = mass;
        this.position = position;
        this.velocity = velocity;
    }
}
