package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class represents physical bodies. It is abstract so that only
 * subclasses can be instantiated, forcing them to declare if they do or do not
 * have physical extent.
 */
public class CollidableAdapter implements Collidable, ModelElement {

    private Vector2D velocityPrev;
    private Point2D positionPrev;
    private Body body;

    public CollidableAdapter( Body body ) {
        this.body = body;
    }

    public void stepInTime( double dt ) {
        // Save the velocity and position before they are updated. This information
        // is used in collision calculations
        if( velocityPrev == null ) {
            velocityPrev = new Vector2D.Double();
        }
        velocityPrev.setComponents( body.getVelocity().getX(), body.getVelocity().getY() );
        if( positionPrev == null ) {
            positionPrev = new Point2D.Double( body.getPosition().getX(), body.getPosition().getY() );
        }
        positionPrev.setLocation( body.getPosition() );
    }

    public Vector2D getVelocityPrev() {
        return velocityPrev;
    }

    public Point2D getPositionPrev() {
        return positionPrev;
    }
}
