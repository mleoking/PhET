/**
 * Class: Gravity
 * Package: edu.colorado.phet.idealgas.model
 * Author: Another Guy
 * Date: Jul 19, 2004
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.math.Vector2D;

public class Gravity /*extends Force2D */implements Force {

    private Vector2D acceleration;

    public Gravity( float  amt ) {
        this.setAmt( amt );
    }

    public void act( Particle p ) {
        p.setAcceleration( p.getAcceleration().add( acceleration ));
    }

    public double getAmt() {
        return acceleration.getY();
    }

    public void setAmt( float  amt ) {
        // TODO: Note that amt is negated because the screen coordinate system has
        // y positive going down. Fix with the coordinate transformation work
        this.acceleration = new Vector2D.Double( 0, -amt );
    }
}
