/** Sam Reid*/
package edu.colorado.phet.movingman.utils;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 6, 2004
 * Time: 12:35:51 PM
 * Copyright (c) Sep 6, 2004 by Sam Reid
 */
public class RectangleUtils {
    public static Rectangle2D.Double toRectangle2DDouble( Rectangle2D output2D ) {
        if( output2D instanceof Rectangle2D.Double ) {
            return (Rectangle2D.Double)output2D;
        }
        else {
            return new Rectangle2D.Double( output2D.getX(), output2D.getY(), output2D.getWidth(), output2D.getHeight() );
        }
    }
}
