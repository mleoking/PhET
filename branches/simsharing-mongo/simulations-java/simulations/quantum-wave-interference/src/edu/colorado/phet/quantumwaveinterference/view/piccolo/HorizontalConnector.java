// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view.piccolo;

import edu.colorado.phet.common.piccolophet.nodes.ConnectorNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jan 23, 2006
 * Time: 9:56:53 AM
 */

public class HorizontalConnector extends ConnectorNode {
    public HorizontalConnector( PNode src, PNode dst ) {
        super( src, dst );
    }

    protected void updateShape( Point2D r1c, Point2D r2c ) {
        double xMin = Math.min( r1c.getX(), r2c.getX() );
        double xMax = Math.max( r1c.getX(), r2c.getX() );
        double width = xMax - xMin;
        Rectangle2D.Double rect = new Rectangle2D.Double( xMin, r1c.getY(), width, 20 );
        super.setPathTo( rect );
    }
}
