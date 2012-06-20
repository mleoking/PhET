package edu.colorado.phet.common.phetcommon.math;

/**
 * A mathematical plane determined by a normal vector to the plane and the distance to the closest
 * point on the plane to the origin.
 * <p/>
 * Uses the equation dot( normal, x ) == distance
 *
 * @author Jonathan Olson
 */
public class PlaneD {
    public final ImmutableVector3D normal;
    public final double distance;

    public static final PlaneD XY = new PlaneD( ImmutableVector3D.Z_UNIT, 0 );
    public static final PlaneD XZ = new PlaneD( ImmutableVector3D.Y_UNIT, 0 );
    public static final PlaneD YZ = new PlaneD( ImmutableVector3D.X_UNIT, 0 );

    public PlaneD( ImmutableVector3D normal, double distance ) {
        this.normal = normal;
        this.distance = distance;
    }

    public ImmutableVector3D intersectWithRay( Ray3D ray ) {
        return ray.pointAtDistance( ray.distanceToPlane( this ) );
    }

    // NOTE: will return null if points are collinear
    public static PlaneD fromTriangle( ImmutableVector3D a, ImmutableVector3D b, ImmutableVector3D c ) {
        ImmutableVector3D normal = ( c.minus( a ) ).cross( b.minus( a ) );
        if ( normal.magnitude() == 0 ) {
            return null;
        }
        normal = normal.normalized();

        return new PlaneD( normal, normal.dot( a ) );
    }
}
