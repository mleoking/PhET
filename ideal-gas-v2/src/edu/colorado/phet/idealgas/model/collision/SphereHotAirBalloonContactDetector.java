/**
 * Class: SphereHotAirBalloonContactDetector
 * Class: edu.colorado.phet.idealgas.model.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 3:47:14 PM
 */
package edu.colorado.phet.idealgas.model.collision;

import edu.colorado.phet.physics.collision.ContactDetector;
import edu.colorado.phet.physics.collision.SphericalBody;
import edu.colorado.phet.physics.body.Body;
import edu.colorado.phet.idealgas.model.HotAirBalloon;
import edu.colorado.phet.idealgas.model.body.IdealGasParticle;

public class SphereHotAirBalloonContactDetector extends ContactDetector {

    public boolean areInContact( Body bodyA, Body bodyB ) {
        SphericalBody sphere;
        HotAirBalloon balloon;
        if( bodyA instanceof HotAirBalloon ) {
            balloon = (HotAirBalloon)bodyA;
            sphere = (SphericalBody)bodyB;
        }
        else {
            balloon = (HotAirBalloon)bodyB;
            sphere = (SphericalBody)bodyA;
        }
        return areInContact( balloon, sphere );
    }

    protected boolean applies( Body bodyA, Body bodyB ) {
//        return false;
        return bodyA instanceof SphericalBody && bodyB instanceof HotAirBalloon
        || bodyB instanceof SphericalBody && bodyA instanceof HotAirBalloon;
    }

    /**
     *
     */
    private boolean areInContact( HotAirBalloon balloon, SphericalBody sphere ) {
        double sep = balloon.getPosition().distance( sphere.getPosition() );
        double distFromShell = Math.abs( sep - balloon.getRadius() );
        boolean result = distFromShell <= sphere.getRadius();
        if( balloon.containsBody( sphere )
            && sep >= balloon.getRadius() - sphere.getRadius() ) {
            result = true;
        }
        if( !balloon.containsBody( sphere )
            && sep <= balloon.getRadius() + sphere.getRadius() ) {
            result = true;
        }
        return result;
    }
}
