// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 11:39:24 PM
 */

public class DefaultLocationMap implements LocationMap {
    private double radius;

    public DefaultLocationMap( double radius ) {
        this.radius = radius;
    }

    public Point2D getLocation( Point2D modelPt ) {
        return new Point2D.Double( modelPt.getX() - radius, modelPt.getY() - radius );
    }
}
