/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.util;

import edu.colorado.phet.common_semiconductor.math.PhetVector;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Feb 25, 2004
 * Time: 2:27:05 AM
 * Copyright (c) Feb 25, 2004 by Sam Reid
 */
public class RectangleUtils {
    public static PhetVector getCenter( Rectangle2D rect ) {
        return new PhetVector( rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2 );
    }
    
}
