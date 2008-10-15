/**
 * Class: Spring
 * Package: edu.colorado.phet.microwave.model.movement
 * Author: Another Guy
 * Date: Jun 13, 2003
 */
package edu.colorado.phet.microwaves.model.movement;

import edu.colorado.phet.microwaves.coreadditions.Vector2D;

import java.awt.geom.Point2D;

public class Spring {
    private float k;
    private Point2D p1;
    private Point2D p2;
    private Vector2D fP1 = new Vector2D( 0, 0 );
    private Vector2D fP2 = new Vector2D( 0, 0 );

    public Spring( float k ) {
        this.k = k;
    }

    public void setEndpoint1( Point2D p1 ) {
        this.p1 = p1;
        if( p2 != null ) {
            fP1.setX( (float)( p1.getX() - p2.getX() ) * k );
            fP1.setY( (float)( p1.getY() - p2.getY() ) * k );
        }
    }

    public void setEndpoint2( Point2D p2 ) {
        this.p2 = p2;
        if( p1 != null ) {
            fP1.setX( (float)( p2.getX() - p1.getX() ) * k );
            fP1.setY( (float)( p2.getY() - p1.getY() ) * k );
        }
    }

    public Vector2D getForceOnEndpoint1() {
        return fP1;
    }

    public Vector2D getForceOnEndpoint2() {
        return fP2;
    }

}
