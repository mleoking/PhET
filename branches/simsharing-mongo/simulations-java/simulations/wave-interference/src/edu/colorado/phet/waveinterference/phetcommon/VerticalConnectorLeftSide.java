// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.phetcommon;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 23, 2006
 * Time: 9:56:53 AM
 */

public class VerticalConnectorLeftSide extends VerticalConnector {
    public VerticalConnectorLeftSide( PNode src, PNode dst ) {
        super( src, dst );
    }

    protected void updateShape( Point2D r1c, Point2D r2c ) {
        double yMin = Math.min( r1c.getY(), r2c.getY() );
        double yMax = Math.max( r1c.getY(), r2c.getY() );
        double height = yMax - yMin;
        double x = getDestination().getFullBounds().getX();
//        double origX = r1c.getX() - getDestination().getFullBounds().getWidth() / 2.0;
        Rectangle2D.Double rect = new Rectangle2D.Double( x, yMin, 20, height );
        super.setPathTo( rect );
    }
}
