/**
 * Class: SphereSphereContactDetector
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.idealgas.collision;

import java.awt.geom.Point2D;

public class SphereSphereContactDetector implements ContactDetector {

    private Point2D tempPt = new Point2D.Double();

    public boolean applies( CollidableBody bodyA, CollidableBody bodyB ) {
        return ( bodyA instanceof SolidSphere && bodyB instanceof SolidSphere );
    }

    /**
     * Note: This method is not thread-safe, because we use an instance attribute
     * to avoid allocating a new Vector2D on every invocation.
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    public boolean areInContact( CollidableBody bodyA, CollidableBody bodyB ) {
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
        tempPt.setLocation( sbA.getPosition() );
        double distance = tempPt.distance( sbB.getPosition() );
        return ( distance <= sbA.getRadius() + sbB.getRadius() );
    }

    private boolean boundingBoxesOverlap( SphericalBody sbA, SphericalBody sbB ) {
        return ( Math.abs( sbA.getPosition().getX() - sbB.getPosition().getX() ) <= sbA.getRadius() + sbB.getRadius()
                 && Math.abs( sbA.getPosition().getY() - sbB.getPosition().getY() ) <= sbA.getRadius() + sbB.getRadius() );
    }
}
