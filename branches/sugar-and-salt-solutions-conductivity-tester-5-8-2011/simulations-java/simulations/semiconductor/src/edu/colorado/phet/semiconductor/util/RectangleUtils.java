// Copyright 2002-2011, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.util;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;


/**
 * User: Sam Reid
 * Date: Feb 25, 2004
 * Time: 2:27:05 AM
 */
public class RectangleUtils {
    public static Vector2D getCenter( Rectangle2D rect ) {
        return new Vector2D( rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2 );
    }

}
