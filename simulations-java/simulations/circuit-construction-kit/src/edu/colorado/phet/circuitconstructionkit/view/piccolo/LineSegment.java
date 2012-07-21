// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:48:42 AM
 */
public class LineSegment {
    public static Shape getSegment( Point2D p1, Point2D p2, double thickness ) {
        return getSegment( p1.getX(), p1.getY(), p2.getX(), p2.getY(), thickness );
    }

    public static Shape getSegment( double x1, double y1, double x2, double y2, double thickness ) {
        Vector2D vec = new Vector2D( x2 - x1, y2 - y1 );
        Vector2D norm = vec.getNormalVector().getInstanceOfMagnitude( thickness / 2 );
        DoubleGeneralPath doublePath = new DoubleGeneralPath( x1 + norm.getX(), y1 + norm.getY() );

        doublePath.lineToRelative( vec.getX(), vec.getY() );
        Vector2D n2 = norm.times( -2 );
        doublePath.lineToRelative( n2.getX(), n2.getY() );
        Vector2D rev = vec.times( -1 );
        doublePath.lineToRelative( rev.getX(), rev.getY() );
        doublePath.lineTo( x1 + norm.getX(), y1 + norm.getY() );
        return doublePath.getGeneralPath();
    }
}
