/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Point2D;

/**
 * This adapter class is provided so that various subclasses of Particle can
 * be Collidable with a minimum amount of effort.
 * 
 * Example:
 *  class MyModelElement extends Particle implements Collidable {
 *      private CollidableAdapter collidableAdapter;
 *
 *      public MyModelElement() {
 *          collidableAdapter = new CollidableAdapter( this );
 *      }
 *
 *      public void stepInTime( double dt ) {
 *          collidableAdapter.stepInTime( dt );
 *      }
 *
 *      public Point2D getPositionPrev() {
 *          return collidableAdapter.getPositionPrev();
 *      }
 *
 *      public Vector2D getVelocityPrev() {
 *          return collidableAdapter.getVelocityPrev();
 *      }
 */
public class CollidableAdapter implements Collidable, ModelElement {

    private Vector2D velocityPrev;
    private Point2D positionPrev;
    private Particle particle;
    private boolean init;

    public CollidableAdapter( Particle particle ) {
        this.particle = particle;
        velocityPrev = new Vector2D.Double( particle.getVelocity() );
        positionPrev = new Point2D.Double( particle.getPosition().getX(), particle.getPosition().getY());
    }

    /**
     * This should be called by the stepInTime() method of the ModelElement in which this object
     * is contained. It should be called before the ModelElement updates its velocity and position.
     * @param dt
     */
    public void stepInTime( double dt ) {
        // Save the velocity and position before they are updated. This information
        // is used in collision calculations
        velocityPrev.setComponents( particle.getVelocity().getX(), particle.getVelocity().getY() );
        positionPrev.setLocation( particle.getPosition() );
    }

    private void init() {
        stepInTime( 0 );
        init = true;
    }
    public Vector2D getVelocityPrev() {
        if( !init ) {
            init();
        }
        return velocityPrev;
    }

    public Point2D getPositionPrev() {
        if( !init ) {
            init();
        }
        return positionPrev;
    }
}
