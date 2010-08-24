/*  */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 10:10:38 AM
 */

public class RampUtil {

    public static Color transparify( Color c, int alpha ) {
        return new Color( c.getRed(), c.getGreen(), c.getBlue(), alpha );
    }

    public static Line2D.Double getInstanceForLength( Line2D.Double line, double newLength ) {
        ImmutableVector2D vec = new ImmutableVector2D( line.getP1(), line.getP2() );
        Point2D halfwayPoint = vec.getScaledInstance( 0.5 ).getDestination( line.getP1() );
        AbstractVector2DInterface halfNew = vec.getInstanceOfMagnitude( newLength / 2.0 );
        Point2D newEnd = halfNew.getDestination( halfwayPoint );
        Point2D newStart = halfNew.getScaledInstance( -1 ).getDestination( halfwayPoint );
        return new Line2D.Double( newStart, newEnd );
    }

    public static Color inverseColor( Color color ) {
        return new Color( 255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue() );
    }
}
