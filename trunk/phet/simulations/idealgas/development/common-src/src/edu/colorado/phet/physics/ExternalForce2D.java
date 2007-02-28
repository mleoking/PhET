/*
 * Class: ExternalForce2D
 * Package: edu.colorado.phet.physics
 *
 * Created by: Ron LeMaster
 * Date: Oct 21, 2002
 */
package edu.colorado.phet.physics;

import edu.colorado.phet.physics.body.Particle;

/**
 * Represents a 2 dimensional force
 */
public class ExternalForce2D extends Force {

    private Vector2D rep = new Vector2D();

    public void act( Particle p ) {
        Vector2D accelerationDelta = rep.multiply( 1 / p.getMass() );
        Vector2D initAcceleration = p.getAcceleration();
        Vector2D newAcceleration = initAcceleration.add( accelerationDelta );
        p.setAcceleration( newAcceleration );
    }

    public float  getX() {
        return rep.getX();
    }

    public float  getY() {
        return rep.getY();
    }

    public void setX( float  x ) {
        rep.setX( x );
    }

    public void setY( float  y ) {
        rep.setY( y );
    }
}
