/**
 * Class: SphereHollowSphereContactDetector
 * Class: edu.colorado.phet.collision
 * User: Ron LeMaster
 * Date: Sep 19, 2004
 * Time: 8:32:34 PM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.HollowSphere;
import edu.colorado.phet.idealgas.model.SphericalBody;

public class SphereHollowSphereContactDetector extends ContactDetector {

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

        double distSq = sphere.getPosition().distanceSq( hollowSphere.getPosition() );
        double distPrevSq = sphere.getPositionPrev().distanceSq( hollowSphere.getPositionPrev() );
        double radSq = hollowSphere.getRadius() * hollowSphere.getRadius();
        if( ( distSq > radSq && distPrevSq < radSq )
            || ( distSq < radSq && distPrevSq > radSq ) ) {
            result = true;
        }
        return result;
    }

    protected boolean applies( CollidableBody bodyA, CollidableBody bodyB ) {
        return ( bodyA instanceof HollowSphere && bodyB instanceof SphericalBody )
               || ( bodyA instanceof SphericalBody && bodyB instanceof HollowSphere );
    }
}
