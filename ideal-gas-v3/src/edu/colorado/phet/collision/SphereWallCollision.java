/**
 * Class: SphereBoxCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 12:35:39 PM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.idealgas.model.IdealGasModel;

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


        switch( contactType ) {
            case SphereWallExpert.RIGHT_SIDE:
                {
                    sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
                    double wx = wallBounds.getMaxX();
                    double dx = wx - ( sx - r );
                    double newX = sx + ( dx * 2 );
                    sphere.setPosition( newX, sphere.getPosition().getY() );
                    break;
                }
            case SphereWallExpert.LEFT_SIDE:
                {
                    sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
                    double wx = wallBounds.getMinX();
                    double dx = ( sx + r ) - wx;
                    double newX = sx - ( dx * 2 );
                    sphere.setPosition( newX, sphere.getPosition().getY() );
                    break;
                }
            case SphereWallExpert.TOP:
                {
                    sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
                    double wy = wallBounds.getMinY();
                    double dy = ( sy + r ) - wy;
                    double newY = sy - ( dy * 2 );
                    sphere.setPosition( sphere.getPosition().getX(), newY );
                    // Adjust y velocity for potential energy
                    adjustDyForGravity( dy * 2 );
                    break;
                }
            case SphereWallExpert.BOTTOM:
                {
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
