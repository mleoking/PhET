/**
 * Class: SphereBoxCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 12:35:39 PM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;

public class SphereBoxCollision implements Collision {

    static public void register() {
        CollisionFactory.addPrototype( new SphereBoxCollision() );
    }

    private SphericalBody sphere;
    private Box2D box;
    private IdealGasModel model;
    private double dt;

    protected SphereBoxCollision() {
    }

    public SphereBoxCollision( SphericalBody sphere, Box2D box,
                               IdealGasModel model, double dt ) {
        this.sphere = sphere;
        this.box = box;
        this.model = model;
        this.dt = dt;
    }

    public void collide() {

        boolean result = false;
        double sx = sphere.getPosition().getX();
        double sy = sphere.getPosition().getY();
        double r = sphere.getRadius();

        if( ( sx - r ) <= box.getMinX() ) {
            sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
            double wx = box.getMinX();
            double newX = wx + ( sx - wx );
            sphere.setPosition( newX, sphere.getPosition().getY() );
        }
        if( ( sx + r ) >= box.getMaxX() ) {
            sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
            double wx = box.getMaxX();
            double newX = wx + ( sx - wx );
            sphere.setPosition( newX, sphere.getPosition().getY() );
        }
        if( ( sy - r ) <= box.getMinY() ) {
            sphere.setVelocity( sphere.getVelocity().getX(), -sphere.getVelocity().getY() );
            double wy = box.getMinY();
            double newY = wy + ( sy - wy );
            sphere.setPosition( sphere.getPosition().getX(), newY );

        }
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


//        Wall collidingWall = box.collideWithParticle( sphere, dt );
//        if( collidingWall instanceof VerticalWall ) {
//            sphere.setVelocity( -sphere.getVelocity().getX(), sphere.getVelocity().getY() );
//            double sx= sphere.getPosition().getX();
//            double wx = collidingWall.getPosition().getX();
//            double newX = wx - ( sx - wx );
//            sphere.setPosition( newX, sphere.getPosition().getY() );
//        }


//        System.out.println( "SphereBoxCollision.collide" );
//        if( IdealGasConfig.heatOnlyFromFloor && box.isFloor( collidingWall ) ) {
//            double preKE = sphere.getKineticEnergy();
//            sphere.setVelocity( sphere.getVelocity().scale( 1 + model.getHeatSource() / 10000 ) );
//            double incrKE = sphere.getKineticEnergy() - preKE;
//            model.addKineticEnergyToSystem( incrKE );
//        }

    }

    public Collision createIfApplicable( Particle particleA, Particle particleB,
                                         IdealGasModel model, double dt ) {
        Collision result = null;
        if( particleA instanceof SphericalBody && particleB instanceof Box2D ) {
            result = new SphereBoxCollision( (SphericalBody)particleA, (Box2D)particleB,
                                             model, dt );
        }
        else if( particleB instanceof SphericalBody && particleA instanceof Box2D ) {
            result = new SphereBoxCollision( (SphericalBody)particleB, (Box2D)particleA,
                                             model, dt );

        }
        return result;
    }
}
