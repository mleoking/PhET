/**
 * Class: SphereHollowSphereContactDetector
 * Class: edu.colorado.phet.collision
 * User: Ron LeMaster
 * Date: Sep 19, 2004
 * Time: 8:32:34 PM
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.idealgas.model.HollowSphere;

public class SphereHollowSphereContactDetector implements ContactDetector {

    public boolean areInContact( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean result = false;

        if( !applies( bodyA, bodyB ) ) {
            return false;
        }

        if( bodyA.getClass() == bodyB.getClass() ) {
            throw new RuntimeException( "bad arguments" );
        }
        HollowSphere hollowSphere = null;
        SphericalBody sphere = null;
        if( bodyA instanceof HollowSphere ) {
            hollowSphere = (HollowSphere)bodyA;
            if( bodyB instanceof SphericalBody ) {
                sphere = (SphericalBody)bodyB;
            }
            else {
                throw new RuntimeException( "bad arguments" );
            }
        }
        if( bodyB instanceof HollowSphere ) {
            hollowSphere = (HollowSphere)bodyB;
            if( bodyA instanceof SphericalBody ) {
                sphere = (SphericalBody)bodyA;
            }
            else {
                throw new RuntimeException( "bad arguments" );
            }
        }
        if( hollowSphere == null || sphere == null ) {
            throw new RuntimeException( "bad arguments" );
        }

        double dist = sphere.getPosition().distance( hollowSphere.getPosition() );
//        double distPrev = sphere.getPositionBeforeTimeStep().distance( hollowSphere.getPositionBeforeTimeStep() );
        double distPrev = sphere.getPositionPrev().distance( hollowSphere.getPositionPrev() );
        double radS = sphere.getRadius();
        double radH = hollowSphere.getRadius();
        if( ( dist + radS >= radH && distPrev + radS < radH )
            || ( dist - radS <= radH && distPrev - radS > radH ) ) {
            result = true;
        }

//        double distSq = sphere.getPosition().distanceSq( hollowSphere.getPosition() );
//        double distPrevSq = sphere.getPositionPrev().distanceSq( hollowSphere.getPositionPrev() );
//        double radSq = hollowSphere.getRadius() * hollowSphere.getRadius();
//
//        // Account for the radius of the sphere
//        distSq += distSq < radSq ?  sphere.getRadius() : -sphere.getRadius();
//        distPrevSq += distPrevSq < radSq ?  sphere.getRadius() : -sphere.getRadius();

//        if( ( distSq > radSq && distPrevSq < radSq )
//            || ( distSq < radSq && distPrevSq > radSq ) ) {
//            result = true;
//        }
        return result;
    }

    public boolean applies( CollidableBody bodyA, CollidableBody bodyB ) {
        return ( bodyA instanceof HollowSphere && bodyB instanceof SphericalBody )
               || ( bodyA instanceof SphericalBody && bodyB instanceof HollowSphere );
    }
}
