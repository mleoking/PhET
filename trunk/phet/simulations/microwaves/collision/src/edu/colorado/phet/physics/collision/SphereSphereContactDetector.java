/**
 * Class: SphereSphereContactDetector
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.physics.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.coreadditions.Body;


public class SphereSphereContactDetector extends ContactDetector {

    private Vector2D tempVector = new Vector2D();

    protected boolean applies( Body bodyA, Body bodyB ) {
        return ( bodyA instanceof SphericalBody && bodyB instanceof SphericalBody );
    }

    /**
     * Note: This method is not thread-safe, because we use an instance attribute
     * to avoid allocating a new Vector2D on every invocation.
     * @param bodyA
     * @param bodyB
     * @return
     */
    public boolean areInContact( Body bodyA, Body bodyB ) {
        double distance = bodyA.getLocation().distanceSq( bodyB.getLocation() );
        return ( distance <= ((SphericalBody)bodyA).getRadius() + ((SphericalBody)bodyB).getRadius() );
    }
}
