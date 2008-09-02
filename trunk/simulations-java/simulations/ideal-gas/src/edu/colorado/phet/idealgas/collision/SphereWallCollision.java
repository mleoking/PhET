/**
 * Class: SphereBoxCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 12:35:39 PM
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Performs collision mechanics between a sphere and a wall
 */
public class SphereWallCollision implements Collision {

    private SphericalBody sphere;
    private Wall wall;
    private IdealGasModel model;
    private int contactType;

    public SphereWallCollision( SphericalBody sphere, Wall wall, int contactType, IdealGasModel model ) {
        this.sphere = sphere;
        this.wall = wall;
        this.model = model;
        this.contactType = contactType;
    }


    public void collide() {
        double sx = sphere.getPosition().getX();
        double sy = sphere.getPosition().getY();
        double r = sphere.getRadius();
        Rectangle2D wallBounds = wall.getBounds();

        // If the sphere is hitting a corner, rather than a flat side of the wall, we need to handle
        // the collision in a special way
//        if( ( sx < wallBounds.getMinX() || sx > wallBounds.getMaxX() )
//            && ( sy < wallBounds.getMinY() || sy > wallBounds.getMaxY() )) {
        if( false ) {

            System.out.println( "contactType = " + contactType );
            // Get the new velocity of the sphere
            Point2D closestPointOnWall = getClosestPointOnWall( sphere.getPosition(), wall );

            Vector2D loa = new Vector2D.Double( sphere.getPosition().getX() - closestPointOnWall.getX(),
                                                sphere.getPosition().getY() - closestPointOnWall.getY() );

            if( loa.getMagnitude() == 0 ) {
                Vector2D v2 = new Vector2D.Double( sphere.getVelocity() ).normalize().scale( 0.1 );
                Point2D p2 = new Point2D.Double( sphere.getPosition().getX() + v2.getX(),
                                                 sphere.getPosition().getY() + v2.getY() );
                closestPointOnWall = getClosestPointOnWall( p2, wall );
                loa = new Vector2D.Double( p2.getX() - closestPointOnWall.getX(),
                                           p2.getY() - closestPointOnWall.getY() );
            }

            // Make sure loa is directed toward the inside of the wall. This makes the rest of the computations simpler
            if( !wall.getBounds().contains( closestPointOnWall.getX() + loa.getX(), closestPointOnWall.getY() + loa.getY() ) ) {
                loa.scale( -1 );
            }

//            Vector2D tangent = new Vector2D.Double( loa.getY(), -loa.getX() );
//            double alpha = tangent.getAngle() - sphere.getVelocity().getAngle();
            double dTheta = loa.getAngle() - sphere.getVelocity().getAngle();
//            if( alpha > Math.PI / 2 ) {
//                alpha = Math.PI - alpha;
//            }
//            if( alpha < -Math.PI / 2 ) {
//                alpha = -Math.PI + alpha;
//            }
//            double alpha = sphereG

            sphere.getVelocity().rotate( dTheta * 2 ).scale( -1 );

            double angle2 = new Vector2D.Double( sphere.getPosition().getX() - closestPointOnWall.getX(),
                                                 sphere.getPosition().getY() - closestPointOnWall.getY() ).getAngle();

            // Determine if the spehre's CM has penetrated the wall
            double d = sphere.getPosition().distance( closestPointOnWall );
            d = ( wall.getBounds().contains( sphere.getPosition() ) ) ? d : -d;

            // Get the sphere's new position
            Vector2D displacement = loa.normalize().scale( ( sphere.getRadius() + d ) * 2 );
            Point2D newPosition = new Point2D.Double( sphere.getPosition().getX() + displacement.getX(),
                                                      sphere.getPosition().getY() + displacement.getY() );
            sphere.setPosition( newPosition );
            return;
        }

        switch( contactType ) {
            case SphereWallExpert.RIGHT_SIDE: {
                sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
                double wx = wallBounds.getMaxX();
                double dx = wx - ( sx - r );
                double newX = sx + ( dx * 2 );
                sphere.setPosition( newX, sphere.getPosition().getY() );
                break;
            }
            case SphereWallExpert.LEFT_SIDE: {
                sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
                double wx = wallBounds.getMinX();
                double dx = ( sx + r ) - wx;
                double newX = sx - ( dx * 2 );
                sphere.setPosition( newX, sphere.getPosition().getY() );
                break;
            }
            case SphereWallExpert.TOP: {
                sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
                double wy = wallBounds.getMinY();
                double dy = ( sy + r ) - wy;
                double newY = sy - ( dy * 2 );
                sphere.setPosition( sphere.getPosition().getX(), newY );
                // Adjust y velocity for potential energy
                adjustDyForGravity( dy * 2 );
                break;
            }
            case SphereWallExpert.BOTTOM: {
                sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
                double wy = wallBounds.getMaxY();
                double dy = wy - ( sy - r );
                double newY = sy + ( dy * 2 );
                sphere.setPosition( sphere.getPosition().getX(), newY );
                // Adjust y velocity for potential energy
                adjustDyForGravity( dy * 2 );
                break;
            }
            default:
                throw new RuntimeException( "Invalid contact type" );
        }
    }

    /**
     * Note that this only works for walls that are oriented along the x and y axes
     *
     * @param p
     * @param wall
     * @return
     */
    private Point2D getClosestPointOnWall( Point2D p, Wall wall ) {

        double x = 0;
        double y = 0;

        switch( contactType ) {
            case SphereWallExpert.RIGHT_SIDE:
                x = wall.getBounds().getMaxX();
                y = p.getY();
                break;
            case SphereWallExpert.LEFT_SIDE:
                x = wall.getBounds().getMinX();
                y = p.getY();
                break;
            case SphereWallExpert.TOP:
                x = p.getX();
                y = wall.getBounds().getMinY();
                break;
            case SphereWallExpert.BOTTOM:
                x = p.getX();
                y = wall.getBounds().getMaxY();
                break;
            default:
                throw new IllegalArgumentException( "Invalid contact type" );
        }

//        double minDx = Math.min( Math.abs( p.getX() - wall.getBounds().getMinX() ), Math.abs( p.getX() - wall.getBounds().getMaxX() ) );
//        double minDy = Math.min( Math.abs( p.getY() - wall.getBounds().getMinY() ), Math.abs( p.getY() - wall.getBounds().getMaxY() ) );
//        if( minDx < minDy ) {
//            y = p.getY();
//            if( Math.abs( p.getX() - wall.getBounds().getMinX() ) < Math.abs( p.getX() - wall.getBounds().getMaxX() ) ) {
//                x = wall.getBounds().getMinX();
//            }
//            else {
//                x = wall.getBounds().getMaxX();
//            }
//        }
//        else {
//            x = p.getX();
//            if( Math.abs( p.getY() - wall.getBounds().getMinY() ) < Math.abs( p.getY() - wall.getBounds().getMaxY() ) ) {
//                y = wall.getBounds().getMinY();
//            }
//            else {
//                y = wall.getBounds().getMaxY();
//            }
//        }
        return new Point2D.Double( x, y );
    }

    /**
     * Returns the new velocity of a particle reflected off the top or bottom of the wall, adjusted for potential
     * energy, and gravity. The velocity of the sphere is assumed to be what it was if the sphere had not been
     * reflected against a horizontal surface. That is, if it is bouncing off the floor of the wall, it will be going
     * as fast as it would if the floor were note there. But since it would actually be above the floor, it should
     * be going much slower. We make the correctin by changing the kinetic energy of the sphere in the y direction
     * by an amount equal to the change in kinetic energy that would occur if the sphere were moved from the
     * non-reflected position to its actual, reflected, position
     *
     * @param dy The vertical distance the molecule has been moved byt the collision.
     */
    private void adjustDyForGravity( double dy ) {
        double m = sphere.getMass();
        double g = model.getGravity().getAmt();
        // Kinetic energy of sphere prior to correction
        double ke = sphere.getMass() * sphere.getVelocity().getY() * sphere.getVelocity().getY() / 2;
        // Change in potential energy when sphere is reflected
        double dpe = dy * g * sphere.getMass();
        double vy = 0;
        if( ke >= dpe ) {
            vy = Math.sqrt( ( 2 / m ) * ( ke - dpe ) ) * MathUtil.getSign( sphere.getVelocity().getY() );
            sphere.setVelocity( sphere.getVelocity().getX(), vy );
        }
        else {
            // This indicates an anomoly in the collision. Right now, I don't know just what to do about it 
//            System.out.println( "ke < dpe" );
        }
    }
}
