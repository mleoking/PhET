/**
 * Class: SphereSphereContactDetector
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.physics.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;

//import edu.colorado.phet.physics.Vector2D;
//import edu.colorado.phet.physics.body.Body;

public class SphereSphereContactDetector extends ContactDetector {

    private Point2D.Float tempVector = new Point2D.Float();
//    private Vector2D tempVector = new Vector2D();

    protected boolean applies( Body bodyA, Body bodyB ) {
        return ( bodyA instanceof SphericalBody && bodyB instanceof SphericalBody );
    }

    /**
     * Note: This method is not thread-safe, because we use an instance attribute
     * to avoid allocating a new Vector2D on every invocation.
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    public boolean areInContact( Body bodyA, Body bodyB ) {
        SphericalBody sbA = (SphericalBody)bodyA;
        SphericalBody sbB = (SphericalBody)bodyB;

        // perform bounding box check, first
        if( boundingBoxesOverlap( sbA, sbB ) ) {
            return spheresOverlap( sbA, sbB );
        }
        else {
            return false;
        }
    }

    private boolean spheresOverlap( SphericalBody sbA, SphericalBody sbB ) {
//        tempVector.setX( sbA.getPosition().getX() );
//        tempVector.setY( sbA.getPosition().getY() );
//        float distance = tempVector.distance( sbB.getPosition() );
        // todo: do distanceSq to save time
        double distance = sbA.getPosition().distance( sbB.getPosition());
        return ( distance <= sbA.getRadius() + sbB.getRadius() );
    }


    private boolean boundingBoxesOverlap( SphericalBody sbA, SphericalBody sbB ) {
        return ( Math.abs( sbA.getPosition().getX() - sbB.getPosition().getX() ) <= sbA.getRadius() + sbB.getRadius()
                 && Math.abs( sbA.getPosition().getY() - sbB.getPosition().getY() ) <= sbA.getRadius() + sbB.getRadius() );
    }

}
