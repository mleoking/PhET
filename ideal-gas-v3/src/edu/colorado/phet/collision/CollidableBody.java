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
    private Vector2D velocityPrev;
    private Point2D positionPrev;

    protected CollidableBody() {
    }

    protected CollidableBody( Point2D position, Vector2D velocity,
                              Vector2D acceleration, double mass, double charge ) {
        super( position, velocity, acceleration, mass, charge );
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable( boolean collidable ) {
        this.collidable = collidable;
    }

    public void setVelocity( Vector2D velocity ) {
        setVelocity( getVelocity().getX(), getVelocity().getY() );
    }

    public void setVelocity( double vx, double vy ) {
        if( velocityPrev== null ) {
            velocityPrev = new Vector2D.Double();
        }
        velocityPrev.setComponents( getVelocity().getX(), getVelocity().getY() );
        super.setVelocity( vx, vy );
    }

    public Vector2D getVelocityPrev() {
        return velocityPrev;
    }

    public void setPosition( double x, double y ) {
        if( positionPrev == null ) {
             positionPrev = new Point2D.Double();
        }
        positionPrev.setLocation( x, y );
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }

    public Point2D getPositionPrev() {
        return positionPrev;
    }

    public abstract double getContactOffset( Body body );

}
