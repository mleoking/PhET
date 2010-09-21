/**
 * Class: SphereBoxCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 12:35:39 PM
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.IdealGasModel;

public class SphereBoxCollision implements Collision {

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private SphericalBody sphere;
    private Box2D box;
    private IdealGasModel model;

    public SphereBoxCollision( SphericalBody sphere, Box2D box, IdealGasModel model ) {
        this.sphere = sphere;
        this.box = box;
        this.model = model;
    }

    public void collide() {
        double sx = sphere.getPosition().getX();
        double sy = sphere.getPosition().getY();
        double spx = sphere.getPositionPrev().getX();
        double spy = sphere.getPositionPrev().getY();
        double r = sphere.getRadius();

        if( box.isInOpening( sphere ) ) {
            return;
        }

        // Check for contact with each of the walls
//        boolean leftWall = ( sx - r ) <= box.getMinX();
//        boolean rightWall = ( sx + r ) >= box.getMaxX();
//        boolean topWall = ( sy - r ) <= box.getMinY();
//        boolean bottomWall = ( sy + r ) >= box.getMaxY();

//        boolean leftWall = ( sx - r ) <= box.getMinX() && ( spx - r ) > box.getMinX();
//        boolean rightWall = ( sx + r ) >= box.getMaxX() && ( spx + r ) < box.getMaxX();
//        boolean topWall = ( sy - r ) <= box.getMinY() && ( spy - r ) > box.getMinY();
//        boolean bottomWall = ( sy + r ) >= box.getMaxY() && ( spy + r ) < box.getMaxY();

        boolean leftWall = false;
        boolean rightWall = false;
        boolean topWall = false;
        boolean bottomWall = false;
        if( true || !( sphere instanceof GasMolecule ) || box.containsBody( sphere ) /* (GasMolecule)sphere ).isInBox()*/ ) {
//        if( !( sphere instanceof GasMolecule ) || ( (GasMolecule)sphere ).isInBox() ) {
            leftWall = ( sx - r ) <= box.getMinX();
            rightWall = ( sx + r ) >= box.getMaxX();
            topWall = ( sy - r ) <= box.getMinY();
            bottomWall = ( sy + r ) >= box.getMaxY();
        }
        // Is the sphere hitting the box from outside
//        else if( sphere instanceof GasMolecule && !( (GasMolecule)sphere ).isInBox() ) {
//            leftWall = ( sx + r ) >= box.getMinX() && (sy - r ) >= box.getMinY() && (sy + r) <= box.getMaxY();
//            rightWall = ( sx - r ) <= box.getMaxX() && (sy - r ) >= box.getMinY() && (sy + r) <= box.getMaxY();
//            topWall = ( sy + r ) >= box.getMinY() && (sx-r) >= box.getMinX() && (sx+r) <= box.getMaxX();
//            bottomWall = ( sy - r ) <= box.getMaxY()&& (sx-r) >= box.getMinX() && (sx+r) <= box.getMaxX();
//
//            System.out.println( "(bottomWall|| topWall||rightWall||leftWall = " + ( bottomWall || topWall || rightWall || leftWall ));
//            if( bottomWall || topWall || rightWall || leftWall ) {
//                System.out.println( "SphereBoxCollision.collide" );
//            }
//        }

        // Collision with left wall?
        if( leftWall && !rightWall ) {

            sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
            double wx = box.getMinX();
            double dx = wx - ( sx - r );
            double newX = sx + ( dx * 2 );
            sphere.setPosition( newX, sphere.getPosition().getY() );

            // Handle giving particle kinetic energy if the wall is moving
            if( model.isWorkDoneByMovingWall() ) {
                double vx0 = sphere.getVelocity().getX();
                double vx1 = vx0 + box.getLeftWallVx();
                double energyPre = sphere.getKineticEnergy();
                sphere.setVelocity( vx1, sphere.getVelocity().getY() );
                double energyPost = sphere.getKineticEnergy();

                // Add the energy to the system, so it doesn't get
                // taken back out when energy conservation is performed
                model.addKineticEnergyToSystem( energyPost - energyPre );
            }
        }

        // Collision with right wall?
        if( rightWall && !leftWall ) {
            sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
            double wx = box.getMaxX();
            double dx = ( sx + r ) - wx;
            double newX = sx - ( dx * 2 );
            sphere.setPosition( newX, sphere.getPosition().getY() );
        }

        // Collision with top wall?
        if( topWall && !bottomWall ) {
            sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
            double wy = box.getMinY();
            double dy = wy - ( sy - r );
            double newY = sy + ( dy * 2 );
            sphere.setPosition( sphere.getPosition().getX(), newY );
            // Adjust y velocity for potential energy
            adjustDyForGravity( dy * 2 );
        }

        if( bottomWall && !topWall ) {
            sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
            double wy = box.getMaxY();
            double dy = ( sy + r ) - wy;
            double newY = sy - ( dy * 2 );
            sphere.setPosition( sphere.getPosition().getX(), newY );
            // Adjust y velocity for potential energy
            adjustDyForGravity( dy * 2 );

            // Here's where we handle adding heat on the floor
            // todo: probably not the best place for this
            if( IdealGasConfig.HEAT_ONLY_FROM_FLOOR ) {
                double preKE = sphere.getKineticEnergy();
                sphere.setVelocity( sphere.getVelocity().scale( 1 + model.getHeatSource() / 10000 ) );
                double incrKE = sphere.getKineticEnergy() - preKE;
                model.addKineticEnergyToSystem( incrKE );
            }
        }
    }

    /**
     * Returns the new velocity of a particle reflected off the top or bottom of the box, adjusted for potential
     * energy, and gravity. The velocity of the sphere is assumed to be what it was if the sphere had not been
     * reflected against a horizontal surface. That is, if it is bouncing off the floor of the box, it will be going
     * as fast as it would if the floor were note there. But since it woulb actually be above the floor, it should
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
