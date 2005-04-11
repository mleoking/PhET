package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;

/**
 * This abstract class represents physical bodies. It is abstract so that only
 * subclasses can be instantiated, forcing them to declare if they do or do not
 * have physical extent.
 */
public abstract class CollidableBody extends Body {

    private boolean collidable = true;

    protected CollidableBody() {
    }

    protected CollidableBody( Point2D position, Vector2D velocity,
                              Vector2D acceleration, double mass, double charge ) {
        //    protected CollidableBody( Point2D.Double position, Vector2D.Double velocity,
        //                    Vector2D.Double acceleration, double mass, double charge ) {
        super( position, velocity, acceleration, mass, charge );
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable( boolean collidable ) {
        this.collidable = collidable;
    }

    public abstract double getContactOffset( Body body );

}
