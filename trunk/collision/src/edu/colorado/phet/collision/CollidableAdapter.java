package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class represents physical particles. It is abstract so that only
 * subclasses can be instantiated, forcing them to declare if they do or do not
 * have physical extent.
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
