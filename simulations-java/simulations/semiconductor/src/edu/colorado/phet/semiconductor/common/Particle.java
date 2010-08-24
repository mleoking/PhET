/*, 2003.*/
package edu.colorado.phet.semiconductor.common;


import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Dec 31, 2003
 * Time: 2:48:10 PM
 */
public class Particle extends SimpleObservable implements ModelElement {
    AbstractVector2DInterface position;
    AbstractVector2DInterface velocity;
    Vector2D acceleration;

    public Particle( double x, double y ) {
        this.position = new Vector2D( x, y );
        this.velocity = new Vector2D();
        this.acceleration = new Vector2D();
    }

    public Particle( AbstractVector2DInterface position ) {
        this( position.getX(), position.getY() );
    }

    public AbstractVector2DInterface getPosition() {
        return position;
    }

    public void stepInTime( double dt ) {
        //acceleration doesn't change here.
        AbstractVector2DInterface dv = acceleration.getScaledInstance( dt );
        this.velocity = velocity.getAddedInstance( dv );
        AbstractVector2DInterface dx = velocity.getScaledInstance( dt );
        this.position = position.getAddedInstance( dx );
        notifyObservers();
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public void setPosition( double x, double y ) {
        this.position = new Vector2D( x, y );
    }

    public void translate( double dx, double dy ) {
        setPosition( getX() + dx, getY() + dy );
    }

}
