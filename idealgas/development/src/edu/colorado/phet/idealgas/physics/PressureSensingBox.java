/*
 * Class: PressureSensingBox
 * Package: edu.colorado.phet.physicaldomain.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Oct 29, 2002
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.collision.SphericalBody;

/**
 *
 */
public class PressureSensingBox extends Box2D {

    private ScalarDataRecorder pressureRecorder = new ScalarDataRecorder();

    /**
     * Constructor
     */
    public PressureSensingBox( Vector2D corner1, Vector2D corner2 ) {
        super( corner1, corner2 );
    }

    public void setPhysicalSystem( PhysicalSystem physicalSystem ) {
        super.setPhysicalSystem( physicalSystem );
    }

    /**
     *
     */
    public void clear() {
        pressureRecorder.clear();
    }

    /**
     *
     */
    public float getPressure() {
        float perimeter = (( this.getMaxY() - this.getMinY() )
                            + ( this.getMaxX() - this.getMinX() )) * 2 ;
        float pressure = pressureRecorder.getDataTotal();
        return pressure / perimeter;
    }

    /**
     * TODO: rewrite this to use methods in BOX2D that find closest wall
     * @param particle
     */
    public void collideWithParticle( SphericalBody particle ) {

        // Handle the change in momentum of the particle
        super.collideWithParticle( particle );

        // Record the force of the collision
        float newVelocityX = particle.getVelocity().getX();
        float newVelocityY = particle.getVelocity().getY();
        float particleMomentum = 0;

        // Determine which wall of the box the particle is colliding with
        Vector2D closestCorner = this.getClosestCorner( particle.getPosition() );

        // Is it colliding with a corner?
        if( Math.abs( Math.abs( closestCorner.getX() - particle.getPosition().getX() )
                      - Math.abs( closestCorner.getY() - particle.getPosition().getY() ) )
                < s_collisionTolerance ) {
            particleMomentum += Math.abs( newVelocityX ) * particle.getMass();
            particleMomentum += Math.abs( newVelocityY ) * particle.getMass();
        }

        // Colliding with a vertical wall?
        else if( Math.abs( closestCorner.getX() - particle.getPosition().getX() )
                < Math.abs( closestCorner.getY() - particle.getPosition().getY() ) ) {
            particleMomentum += Math.abs( newVelocityX ) * particle.getMass();
        }

        // It must be colliding with a horizontal wall
        else {
            particleMomentum += Math.abs( newVelocityY ) * particle.getMass();
        }

        //this.addDataRecordEntry( particle.getClass(), particleMomentum );
        this.pressureRecorder.addDataRecordEntry( particleMomentum );
    }

    //
    // Static fields and methods
    //
    double s_collisionTolerance = 2.0;
}
