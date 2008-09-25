/*, 2003.*/
package edu.colorado.phet.semiconductor.common;



import edu.colorado.phet.semiconductor.phetcommon.model.simpleobservable.SimpleObservable;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: Dec 31, 2003
 * Time: 2:48:10 PM
 */
public class Particle extends SimpleObservable implements ModelElement {
    AbstractVector2D position;
    AbstractVector2D velocity;
    Vector2D.Double acceleration;

    public Particle( double x, double y ) {
        this.position = new Vector2D.Double( x, y );
        this.velocity = new Vector2D.Double();
        this.acceleration = new Vector2D.Double();
    }

    public Particle( AbstractVector2D position ) {
        this( position.getX(), position.getY() );
    }

    public AbstractVector2D getPosition() {
        return position;
    }

    public void stepInTime( double dt ) {
        //acceleration doesn't change here.
        AbstractVector2D dv = acceleration.getScaledInstance( dt );
        this.velocity = velocity.getAddedInstance( dv );
        AbstractVector2D dx = velocity.getScaledInstance( dt );
        this.position = position.getAddedInstance( dx );
        updateObservers();
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public void setPosition( double x, double y ) {
        this.position = new Vector2D.Double( x, y );
    }

    public void translate( double dx, double dy ) {
        setPosition( getX() + dx, getY() + dy );
    }

}
