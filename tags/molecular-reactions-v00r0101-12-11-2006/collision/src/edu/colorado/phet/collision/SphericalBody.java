/**
 * Class: SphericalBody
 * Package: edu.colorado.phet.model.body
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;

/**
 * NOTE: This class is not thread-safe!!!!!
 */
public class SphericalBody extends Body implements Collidable {

    private double radius;
    private CollidableAdapter collidableAdapter;

    public SphericalBody( double radius ) {
        this( new Point2D.Double(),
              new Vector2D.Double(),
              new Vector2D.Double(),
              0,
              radius );
    }

    protected SphericalBody( Point2D center,
                             Vector2D velocity,
                             Vector2D acceleration,
                             double mass,
                             double radius ) {
        super( center, velocity, acceleration, mass, 0 );
        this.radius = radius;
        collidableAdapter = new CollidableAdapter( this );
    }

    public Point2D getCM() {
        return this.getPosition();
    }

    public double getMomentOfInertia() {
        return getMass() * radius * radius * 2 / 5;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius( double radius ) {
        this.radius = radius;
    }

    public Point2D getCenter() {
        return this.getPosition();
    }

    public double getContactOffset( Body body ) {
        return this.getRadius();
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        if( collidableAdapter == null ) {
            System.out.println( "SphericalBody.getPositionPrev" );
        }
        return collidableAdapter.getPositionPrev();
    }

    public void stepInTime( double dt ) {
        collidableAdapter.stepInTime( dt );
        super.stepInTime( dt );    //To change body of overridden methods use File | Settings | File Templates.
    }
}
