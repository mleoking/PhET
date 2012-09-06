// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;

/**
 * Generic sphere model class, with sphere-ray intersection routines
 *
 * @author Jonathan Olson
 */
public @EqualsAndHashCode(callSuper = false) @ToString class SphereF {
    public final Vector3F center;
    public final float radius;

    public SphereF( Vector3F center, float radius ) {
        this.center = center;
        this.radius = radius;
    }

    // Intersection with a 3D ray. Does not return any intersection if the sphere is behind the ray
    public SphereIntersectionResult intersect( Ray3F ray, float epsilon ) {
        Vector3F raydir = ray.dir;
        Vector3F pos = ray.pos;
        Vector3F centerToRay = pos.minus( center );

        // basically, we can use the quadratic equation to solve for both possible hit points (both +- roots are the hit points)
        float tmp = raydir.dot( centerToRay );
        float centerToRayDistSq = centerToRay.magnitudeSquared();
        float det = 4 * tmp * tmp - 4 * ( centerToRayDistSq - radius * radius );
        if ( det < epsilon ) {
            // ray misses sphere entirely
            return null;
        }

        float base = raydir.dot( center ) - raydir.dot( pos );
        float sqt = (float) ( Math.sqrt( det ) / 2 );

        // the "first" entry point distance into the sphere. if we are inside the sphere, it is behind us
        float ta = base - sqt;

        // the "second" entry point distance
        float tb = base + sqt;

        if ( tb < epsilon ) {
            // sphere is behind ray, so don't return an intersection
            return null;
        }

        Vector3F hitPositionB = ray.pointAtDistance( tb );
        Vector3F normalB = hitPositionB.minus( center ).normalized();

        if ( ta < epsilon ) {
            // we are inside the sphere
            // in => out
            return new SphereIntersectionResult( tb, hitPositionB, normalB.negated(), false );
        }
        else {
            // two possible hits
            Vector3F hitPositionA = ray.pointAtDistance( ta );
            Vector3F normalA = hitPositionA.minus( center ).normalized();

            // close hit, we have out => in
            return new SphereIntersectionResult( ta, hitPositionA, normalA, true );
        }
    }

    public Vector3F getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public static class SphereIntersectionResult {
        public float distance;
        public Vector3F hitPoint;
        public Vector3F normal;
        public boolean fromOutside;

        public SphereIntersectionResult( float distance, Vector3F hitPoint, Vector3F normal, boolean fromOutside ) {
            this.distance = distance;
            this.hitPoint = hitPoint;
            this.normal = normal;
            this.fromOutside = fromOutside;
        }

        public float getDistance() {
            return distance;
        }

        public Vector3F getHitPoint() {
            return hitPoint;
        }

        public Vector3F getNormal() {
            return normal;
        }

        public boolean isFromOutside() {
            return fromOutside;
        }
    }
}
