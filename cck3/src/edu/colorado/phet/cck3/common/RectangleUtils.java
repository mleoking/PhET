/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 17, 2004
 * Time: 3:15:06 PM
 * Copyright (c) Jun 17, 2004 by Sam Reid
 */
public class RectangleUtils {
    public static Rectangle expand( Rectangle r, int dx, int dy ) {
        return new Rectangle( r.x - dx, r.y - dy, r.width + dx * 2, r.height + dy * 2 );
    }

    public static Rectangle toRectangle( Rectangle2D b ) {
        if( b instanceof Rectangle ) {
            return (Rectangle)b;
        }
        else {
            return new Rectangle( (int)b.getX(), (int)b.getY(), (int)b.getWidth(), (int)b.getHeight() );
        }
    }

    public static Point getLeftSide( Rectangle bounds ) {
        Point pt = bounds.getLocation();
        pt.translate( 0, bounds.height / 2 );
        return pt;
    }

    public static Point getNorth( Rectangle bounds ) {
        Point pt = bounds.getLocation();
        pt.translate( bounds.width / 2, 0 );
        return pt;
    }

    public static Point getEastSide( Rectangle bounds ) {
        Point pt = bounds.getLocation();
        pt.translate( bounds.width, bounds.height / 2 );
        return pt;
    }
}
