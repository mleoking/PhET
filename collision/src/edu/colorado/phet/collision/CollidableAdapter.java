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

    public CollidableAdapter( Particle particle ) {
        this.particle = particle;
    }

    public void stepInTime( double dt ) {
        // Save the velocity and position before they are updated. This information
        // is used in collision calculations
        if( velocityPrev == null ) {
            velocityPrev = new Vector2D.Double();
        }
        velocityPrev.setComponents( particle.getVelocity().getX(), particle.getVelocity().getY() );
        if( positionPrev == null ) {
            positionPrev = new Point2D.Double( particle.getPosition().getX(), particle.getPosition().getY() );
        }
        positionPrev.setLocation( particle.getPosition() );
    }

    public Vector2D getVelocityPrev() {
        return velocityPrev;
    }

    public Point2D getPositionPrev() {
        return positionPrev;
    }
}
