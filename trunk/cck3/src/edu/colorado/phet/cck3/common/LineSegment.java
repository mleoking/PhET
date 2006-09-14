/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:48:42 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class LineSegment {
    public static Shape getSegment( Point2D p1, Point2D p2, double thickness ) {
        return getSegment( p1.getX(), p1.getY(), p2.getX(), p2.getY(), thickness );
    }

    public static Shape getSegment( double x1, double y1, double x2, double y2, double thickness ) {
        ImmutableVector2D.Double vec = new ImmutableVector2D.Double( x2 - x1, y2 - y1 );
        AbstractVector2D norm = vec.getNormalVector().getInstanceOfMagnitude( thickness / 2 );
        DoubleGeneralPath doublePath = new DoubleGeneralPath( x1 + norm.getX(), y1 + norm.getY() );

        doublePath.lineToRelative( vec.getX(), vec.getY() );
        AbstractVector2D n2 = norm.getScaledInstance( -2 );
        doublePath.lineToRelative( n2.getX(), n2.getY() );
        AbstractVector2D rev = vec.getScaledInstance( -1 );
        doublePath.lineToRelative( rev.getX(), rev.getY() );
        doublePath.lineTo( x1 + norm.getX(), y1 + norm.getY() );
        return doublePath.getGeneralPath();
    }
}
