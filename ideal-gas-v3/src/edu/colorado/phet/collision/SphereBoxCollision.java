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
import edu.colorado.phet.idealgas.model.Wall;

public class SphereBoxCollision implements Collision {

    private SphericalBody sphere;
    private Box2D box;
    private IdealGasModel model;
    private double dt;

    protected SphereBoxCollision() {
        //NOP
    }

    public SphereBoxCollision( SphericalBody sphere, Box2D box,
                               IdealGasModel model, double dt ) {
        this.sphere = sphere;
        this.box = box;
        this.model = model;
        this.dt = dt;
    }

    public void collide() {
        Wall collidingWall = box.collideWithParticle( sphere, dt );
        if( IdealGasConfig.heatOnlyFromFloor && box.isFloor( collidingWall )) {
            double preKE = sphere.getKineticEnergy();
//            double preKE = sphere.getKineticEnergyDouble();
//            IdealGasSystem idealGasSystem = (IdealGasSystem)IdealGasSystem.instance();
            sphere.setVelocity( sphere.getVelocity().scale( 1 + model.getHeatSource() / 10000 ) );
//            sphere.setVelocity( sphere.getVelocity().multiply( 1 + idealGasSystem.getHeatSource() / 10000 ) );
            double incrKE = sphere.getKineticEnergy() - preKE;
//            double incrKE = sphere.getKineticEnergyDouble() - preKE;
            model.addKineticEnergyToSystem( incrKE );
//            idealGasSystem.addKineticEnergyToSystem( incrKE );
        }

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

    //
    // Static fields and methods
    //
    static public void register() {
        CollisionFactory.addPrototype( new SphereBoxCollision() );
    }
}
