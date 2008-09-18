/**
 * Class: Spring Package: edu.colorado.phet.emf.model.movement Author: Another
 * Guy Date: Jun 13, 2003
 */

package edu.colorado.phet.radiowaves.model.movement;

import java.awt.geom.Point2D;

import edu.colorado.phet.common_1200.math.Vector2D;

public class Spring {

    private float k;
    private Point2D p1;
    private Point2D p2;
    private Vector2D.Float fP1 = new Vector2D.Float( 0, 0 );
    private Vector2D.Float fP2 = new Vector2D.Float( 0, 0 );

    public Spring( float k ) {
        this.k = k;
    }

    public void setEndpoint1( Point2D p1 ) {
        this.p1 = p1;
        if ( p2 != null ) {
            fP1.setX( (float) ( p1.getX() - p2.getX() ) * k );
            fP1.setY( (float) ( p1.getY() - p2.getY() ) * k );
        }
    }

    public void setEndpoint2( Point2D p2 ) {
        this.p2 = p2;
        if ( p1 != null ) {
            fP1.setX( (float) ( p2.getX() - p1.getX() ) * k );
            fP1.setY( (float) ( p2.getY() - p1.getY() ) * k );
        }
    }

    public Vector2D.Float getForceOnEndpoint1() {
        return fP1;
    }

    public Vector2D.Float getForceOnEndpoint2() {
        return fP2;
    }

}
