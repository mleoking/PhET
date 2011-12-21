// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

public class Ray3F {
    // the position where the ray is pointed from
    public final ImmutableVector3F pos;

    // the unit vector direction in which the ray is pointed
    public final ImmutableVector3F dir;

    public Ray3F( ImmutableVector3F pos, ImmutableVector3F dir ) {
        this.pos = pos;

        // normalize dir if needed
        this.dir = dir.magnitude() == 1 ? dir : dir.normalized();
    }

    @Override public String toString() {
        return pos.toString() + " => " + dir.toString();
    }
}
