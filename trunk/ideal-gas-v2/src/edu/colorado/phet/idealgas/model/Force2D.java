/**
 * Class: Force2D
 * Package: edu.colorado.phet.idealgas.model
 * Author: Another Guy
 * Date: Jul 19, 2004
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

public class Force2D {

        private Vector2D rep = new Vector2D.Double();

        public void act( Body body ) {
            Vector2D accelerationDelta = rep.scale( 1 / body.getMass() );
            Vector2D initAcceleration = body.getAcceleration();
            Vector2D newAcceleration = initAcceleration.add( accelerationDelta );
            body.setAcceleration( newAcceleration );
        }

        public double getX() {
            return rep.getX();
        }

        public double getY() {
            return rep.getY();
        }

        public void setX( float  x ) {
            rep.setX( x );
        }

        public void setY( float  y ) {
            rep.setY( y );
        }
    }
