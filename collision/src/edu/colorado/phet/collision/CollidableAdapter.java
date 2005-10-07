/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;

import java.awt.geom.Point2D;

/**
 * This adapter class is provided so that various subclasses of Particle can
 * be Collidable with a minimum amount of effort.
 * <p/>
 * Example:
 * class MyModelElement extends Particle implements Collidable {
 * private CollidableAdapter collidableAdapter;
 * <p/>
 * public MyModelElement() {
 * collidableAdapter = new CollidableAdapter( this );
 * }
 * <p/>
 * public void stepInTime( double DT ) {
 * collidableAdapter.stepInTime( DT );
 * }
 * <p/>
 * public Point2D getPositionPrev() {
 * return collidableAdapter.getPositionPrev();
 * }
 * <p/>
 * public Vector2D getVelocityPrev() {
 * return collidableAdapter.getVelocityPrev();
 * }
 */
public class CollidableAdapter implements Collidable {

    private Vector2D velocityPrev;
    private Point2D positionPrev;
    private Particle particle;
    private boolean init;

    public CollidableAdapter( Particle particle ) {
        this.particle = particle;
        velocityPrev = new Vector2D.Double( particle.getVelocity() );
        positionPrev = new Point2D.Double( particle.getPosition().getX(), particle.getPosition().getY() );
    }

    private void init() {
        stepInTime( 0 );
        init = true;
    }

    /**
     * @param dt
     */
    public void stepInTime( double dt ) {
        positionPrev.setLocation( particle.getPosition() );
        velocityPrev.setComponents( particle.getVelocity().getX(), particle.getVelocity().getY() );
    }

    public void updatePosition() {
        positionPrev.setLocation( particle.getPosition() );
    }

    public void updateVelocity() {
        velocityPrev.setComponents( particle.getVelocity().getX(), particle.getVelocity().getY() );
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
