/*
 * Class: Gravity
 * Package: edu.colorado.phet.physics
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.physics;

import edu.colorado.phet.physics.body.Particle;

/**
 * This class represents the acceleration of a body caused by the Earth's gravitational
 * field. It is not really a force, but rather a constant acceleration, so it is really
 * a bit of a HACK!
 */
public class Gravity extends ExternalForce2D {

    private Vector2D acceleration;

    public Gravity( float  amt ) {
        this.setAmt( amt );
    }

    public void act( Particle p ) {
        p.setAcceleration( p.getAcceleration().add( acceleration ));
    }

    public float  getAmt() {
        return acceleration.getY();
    }

    public void setAmt( float  amt ) {

        // TODO: Note that amt is negated because the screen coordinate system has
        // y positive going down. Fix with the coordinate transformation work
        this.acceleration = new Vector2D( 0, -amt );
    }
}
