/**
 * Class: Body
 * Package: edu.colorado.phet.physics.body
 * Author: Another Guy
 * Date: Mar 24, 2003
 */
package edu.colorado.phet.physics.body;

import edu.colorado.phet.physics.Vector2D;

public abstract class Body extends Particle {

    protected Body() {
    }

    protected Body( Vector2D position, Vector2D velocity,
                    Vector2D acceleration, float  mass, float  charge ) {
        super( position, velocity, acceleration, mass, charge );
    }

    public boolean isInContactWithBody( Body body ){
        return false;
    }
//    public abstract boolean isInContactWithBody( Body body );

    public abstract float getContactOffset( Body body );
}
