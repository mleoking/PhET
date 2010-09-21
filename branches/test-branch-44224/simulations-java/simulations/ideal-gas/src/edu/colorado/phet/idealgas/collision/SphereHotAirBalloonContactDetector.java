/**
 * Class: SphereHotAirBalloonContactDetector
 * Class: edu.colorado.phet.collision
 * User: Ron LeMaster
 * Date: Sep 22, 2004
 * Time: 8:10:39 PM
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.idealgas.model.HotAirBalloon;

public class SphereHotAirBalloonContactDetector implements ContactDetector {

    private SphereHollowSphereContactDetector detector = new SphereHollowSphereContactDetector();

    public boolean areInContact( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean result = false;

        if( !applies( bodyA, bodyB ) ) {
            return false;
        }

        if( bodyA.getClass() == bodyB.getClass() ) {
            throw new RuntimeException( "bad arguments" );
        }
        HotAirBalloon balloon = null;
        SphericalBody sphere = null;
        if( bodyA instanceof HotAirBalloon ) {
            balloon = (HotAirBalloon)bodyA;
            if( bodyB instanceof SphericalBody ) {
                sphere = (SphericalBody)bodyB;
            }
            else {
                throw new RuntimeException( "bad arguments" );
            }
        }
        if( bodyB instanceof HotAirBalloon ) {
            balloon = (HotAirBalloon)bodyB;
            if( bodyA instanceof SphericalBody ) {
                sphere = (SphericalBody)bodyA;
            }
            else {
                throw new RuntimeException( "bad arguments" );
            }
        }
        if( balloon == null || sphere == null ) {
            throw new RuntimeException( "bad arguments" );
        }

        if( !balloon.getOpening().contains( sphere.getPosition() ) ) {
            result = detector.areInContact( balloon, sphere );
        }
        return result;
    }

    public boolean applies( CollidableBody bodyA, CollidableBody bodyB ) {
        return ( bodyA instanceof HotAirBalloon && bodyB instanceof SolidSphere )
               || ( bodyA instanceof SolidSphere && bodyB instanceof HotAirBalloon );
    }
}
