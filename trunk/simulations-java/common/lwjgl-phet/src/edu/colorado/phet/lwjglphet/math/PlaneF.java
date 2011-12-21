// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

/**
 * A mathematical plane determined by a normal vector to the plane and the distance to the closest
 * point on the plane to the origin
 */
public class PlaneF {
    public final ImmutableVector3F normal;
    public final float distance;

    public static final PlaneF XY = new PlaneF( ImmutableVector3F.Z_UNIT, 0 );
    public static final PlaneF XZ = new PlaneF( ImmutableVector3F.Y_UNIT, 0 );
    public static final PlaneF YZ = new PlaneF( ImmutableVector3F.X_UNIT, 0 );

    public PlaneF( ImmutableVector3F normal, float distance ) {
        this.normal = normal;
        this.distance = distance;
    }

    public ImmutableVector3F intersectWithRay( Ray3F ray ) {
        return ray.pointAtDistance( ray.distanceToPlane( this ) );
    }
}
