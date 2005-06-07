/**
 * Class: NuclearParticle
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;

import java.awt.geom.Point2D;

public class NuclearParticle extends NuclearModelElement implements Collidable {
    private double radius;
    private CollidableAdapter collidableAdapter;

    public NuclearParticle( Point2D location ) {
        super( location, new Vector2D.Double(), new Vector2D.Double(), 0f, 0f );
        this.radius = RADIUS;
        collidableAdapter = new CollidableAdapter( this );
    }

    public double getRadius() {
        return this.radius;
    }

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public void setVelocity( Vector2D vector2D ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updateVelocity();
        }
        super.setVelocity( vector2D );
    }

    public void setVelocity( double v, double v1 ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updateVelocity();
        }
        super.setVelocity( v, v1 );
    }

    public void setPosition( Point2D point2D ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updatePosition();
        }
        super.setPosition( point2D );
    }

    public void setPosition( double v, double v1 ) {
        if( collidableAdapter != null ) {
            collidableAdapter.updatePosition();
        }
        super.setPosition( v, v1 );
    }

    //
    // Statics
    //
    public final static double RADIUS = 5;
}
