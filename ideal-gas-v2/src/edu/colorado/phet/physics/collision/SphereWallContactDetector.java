/**
 * Class: WallSphereContactExpertTest
 * Package: edu.colorado.phet.lasers.model.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.physics.collision;

import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;

import java.awt.geom.Point2D;
//import edu.colorado.phet.idealgas.model.body.Wall;

/**
 * Determines if a wall and sphere are in contact. Currently, this doesn't take
 * in to account the extent of the wall. In other words, it considers the wall
 * to be infinite in length
 */
public class SphereWallContactDetector extends ContactDetector {

    private Vector2D tempVector = new Vector2D.Double();

    /**
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    protected boolean applies( Body bodyA, Body bodyB ) {
        boolean b = ( ( bodyA instanceof SphericalBody && bodyB instanceof edu.colorado.phet.physics.collision.Wall )
                || ( bodyB instanceof SphericalBody && bodyA instanceof edu.colorado.phet.physics.collision.Wall ) );
        return b;
    }

    /**
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    public boolean areInContact( Body bodyA, Body bodyB ) {
        SphericalBody sphere = null;
        Wall wall = null;
        if( bodyA instanceof Wall && bodyB instanceof SphericalBody ) {
            wall = (Wall)bodyA;
            sphere = (SphericalBody)bodyB;
        }
        else if( bodyA instanceof SphericalBody && bodyB instanceof Wall ) {
            wall = (Wall)bodyB;
            sphere = (SphericalBody)bodyA;
        }

        // Determine if the sphere within a radius distance of the line on which
        // the wall lies
        double x = wall.getPosition().getX();
        double y = wall.getPosition().getY();
        tempVector.setX( Double.isNaN( x ) ? 0 : x );
        tempVector.setY( Double.isNaN( y ) ? 0 : y  );

        tempVector.setComponents( tempVector.getX() - sphere.getPosition().getX(),
                                               tempVector.getY() - sphere.getPosition().getY() );
//        tempVector = tempVector.subtract( sphere.getPosition() );
        float dist = (float)Math.abs( tempVector.dot( wall.getLoaUnit( sphere ) ) );
        boolean result = dist <= sphere.getRadius();

        // If the previous result is true, determine if a line through the sphere's
        // CM perpendicular to the line on which the wall lies passes through the
        // wall itself
        if( result ) {
            double dx = (float)( wall.getEnd1().getX() - wall.getEnd2().getX() );
            double dy = (float)( wall.getEnd1().getY() - wall.getEnd2().getY() );
            double xSphere = sphere.getPosition().getX();
            double ySphere = sphere.getPosition().getY();
            double xSphere2 = xSphere + dy;
            double ySphere2 = ySphere + dx;
            result &= MathUtil.segmentIntersectsLine( wall.getEnd1().getX(),
                                                      wall.getEnd1().getY(),
                                                      wall.getEnd2().getX(),
                                                      wall.getEnd2().getY(),
                                                            xSphere, ySphere,
                                                            xSphere2, ySphere2);
        }
        return result;
    }
}
