/**
 * Class: SphereBoxCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 12:35:39 PM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;

public class SphereBoxCollision implements Collision {

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
        double r = sphere.getRadius();

        if( box.isInOpening( sphere ) ) {
            return;
        }

        // Collision with left wall?
        if( ( sx - r ) <= box.getMinX() ) {
            sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
            double wx = box.getMinX();
            double newX = wx + ( sx - wx );
            sphere.setPosition( newX, sphere.getPosition().getY() );

            // Handle giving particle kinetic energy if the wall is moving
            double vx0 = sphere.getVelocity().getX();
            double vx1 = vx0 + box.getLeftWallVx();
            sphere.setVelocity( vx1, sphere.getVelocity().getY() );

            // Add the energy to the system, so it doesn't get
            // taken back out when energy conservation is performed
            model.addKineticEnergyToSystem( box.getLeftWallVx() );
        }

        // Collision with right wall?
        if( ( sx + r ) >= box.getMaxX() ) {
            sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
            double wx = box.getMaxX();
            double newX = wx + ( sx - wx );
            sphere.setPosition( newX, sphere.getPosition().getY() );
        }

        // Collision with bottom wall?
        if( ( sy - r ) <= box.getMinY() ) {
            sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
            double wy = box.getMinY();
            double newY = wy + ( sy - wy );
            sphere.setPosition( sphere.getPosition().getX(), newY );

        }

        // Collision with top wall?
        if( ( sy + r ) >= box.getMaxY() ) {
            sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
            double wy = box.getMaxY();
            double newY = wy + ( sy - wy );
            sphere.setPosition( sphere.getPosition().getX(), newY );

            // Here's where we handle adding heat on the floor
            // todo: probably not the best place for this
            if( IdealGasConfig.heatOnlyFromFloor ) {
                double preKE = sphere.getKineticEnergy();
                sphere.setVelocity( sphere.getVelocity().scale( 1 + model.getHeatSource() / 10000 ) );
                double incrKE = sphere.getKineticEnergy() - preKE;
                model.addKineticEnergyToSystem( incrKE );
            }
        }
    }
}
