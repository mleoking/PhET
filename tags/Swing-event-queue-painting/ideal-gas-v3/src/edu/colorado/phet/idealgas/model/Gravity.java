/**
 * Class: Gravity
 * Package: edu.colorado.phet.idealgas.model
 * Author: Another Guy
 * Date: Jul 19, 2004
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.mechanics.Body;

import java.util.List;

public class Gravity implements ModelElement {
    //public class Gravity implements Force {
    //public class Gravity implements Force {

    private Vector2D acceleration;
    private IdealGasModel model;

    public Gravity( IdealGasModel model ) {
        this.model = model;
        this.setAmt( 0 );
    }

    public void stepInTime( double dt ) {
        List bodies = model.getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.setAcceleration( body.getAcceleration().add( acceleration ) );
        }
    }

    public double getAmt() {
        return acceleration.getY();
    }

    public void setAmt( double amt ) {
        this.acceleration = new Vector2D.Double( 0, amt );
    }
}
