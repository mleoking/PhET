/**
 * Class: SphereBoxCollision
 * Class: edu.colorado.phet.physics.collision
 * User: Ron LeMaster
 * Date: Apr 4, 2003
 * Time: 12:35:39 PM
 */
package edu.colorado.phet.physics.collision;

//import edu.colorado.phet.controller.Config;
//import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.idealgas.physics.IdealGasSystem;
import edu.colorado.phet.common.model.Particle;

public class SphereBoxCollision implements Collision {

    private SphericalBody sphere;
    private Box2D box;

    protected SphereBoxCollision() {
        //NOP
    }

    public SphereBoxCollision( SphericalBody sphere, Box2D box ) {
        this.sphere = sphere;
        this.box = box;
    }

    public void collide() {
        Wall collidingWall = box.collideWithParticle( sphere );
        if( Config.heatOnlyFromFloor && box.isFloor( collidingWall )) {
            double preKE = sphere.getKineticEnergyDouble();
            IdealGasSystem idealGasSystem = (IdealGasSystem)IdealGasSystem.instance();
            sphere.setVelocity( sphere.getVelocity().multiply( 1 + idealGasSystem.getHeatSource() / 10000 ) );
            double incrKE = sphere.getKineticEnergyDouble() - preKE;
            idealGasSystem.addKineticEnergyToSystem( incrKE );
            System.out.println( "!!!" );
        }

    }

    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision result = null;
        if( particleA instanceof SphericalBody && particleB instanceof Box2D ) {
            result = new SphereBoxCollision( (SphericalBody)particleA, (Box2D)particleB );
        }
        else if( particleB instanceof SphericalBody && particleA instanceof Box2D ) {
            result = new SphereBoxCollision( (SphericalBody)particleB, (Box2D)particleA );
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
