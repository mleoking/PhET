// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

/**
 * A three-dimensional ray (position and direction), with floats
 */
public class Ray3F {
    // the position where the ray is pointed from
    public final Vector3F pos;

    // the unit vector direction in which the ray is pointed
    public final Vector3F dir;

    public Ray3F( Vector3F pos, Vector3F dir ) {
        this.pos = pos;

        // normalize dir if needed
        this.dir = dir.magnitude() == 1 ? dir : dir.normalized();
    }

    // a ray whose position is shifted by the specified distance in the direction of the ray
    public Ray3F shifted( float distance ) {
        return new Ray3F( pointAtDistance( distance ), dir );
    }

    public Vector3F pointAtDistance( float distance ) {
        return pos.plus( dir.times( distance ) );
    }

    public float distanceToPlane( PlaneF plane ) {
        return ( plane.distance - pos.dot( plane.normal ) ) / dir.dot( plane.normal );
    }

    @Override public String toString() {
        return pos.toString() + " => " + dir.toString();
    }

    public Option<Vector3F> intersectWithTriangle( Vector3F a, Vector3F b, Vector3F c ) {
        PlaneF plane = PlaneF.fromTriangle( a, b, c );

        if ( plane == null ) {
            // points collinear, don't form a triangle
            return new None<Vector3F>();
        }

        // find where our ray will intersect with this plane
        Vector3F planePoint = plane.intersectWithRay( this );

        // compute barycentric coordinates
        Vector3F v0 = c.minus( a );
        Vector3F v1 = b.minus( a );
        Vector3F v2 = planePoint.minus( a );

        float dot00 = v0.dot( v0 );
        float dot01 = v0.dot( v1 );
        float dot02 = v0.dot( v2 );

        float dot11 = v1.dot( v1 );
        float dot12 = v1.dot( v2 );

        float p = 1 / ( dot00 * dot11 - dot01 * dot01 );

        // these are the barycentric coordinates
        float u = ( dot11 * dot02 - dot01 * dot12 ) * p;
        float v = ( dot00 * dot12 - dot01 * dot02 ) * p;

        boolean hit = ( u >= 0 ) && ( v >= 0 ) && ( u + v <= 1 );

        if ( hit ) {
            System.out.println( "hit: " + planePoint + " on " + a + ", " + b + ", " + c );
        }

        return hit ? new Some<Vector3F>( planePoint ) : new None<Vector3F>();
    }

    private static boolean approximateCoplanarPointInTriangle( Vector3F a, Vector3F b, Vector3F c, Vector3F point ) {
        float areaA = triangleXYArea( point, b, c );
        float areaB = triangleXYArea( point, c, a );
        float areaC = triangleXYArea( point, a, b );
        float insideArea = triangleXYArea( a, b, c );

        // some area must be "outside" the main triangle (just needs to be close)
        return areaA + areaB + areaC <= insideArea * 1.02;
    }

    private static float triangleXYArea( Vector3F a, Vector3F b, Vector3F c ) {
        return Math.abs( ( ( a.x - c.x ) * ( b.y - c.y ) - ( b.x - c.x ) * ( a.y - c.y ) ) / 2.0f );
    }
}
