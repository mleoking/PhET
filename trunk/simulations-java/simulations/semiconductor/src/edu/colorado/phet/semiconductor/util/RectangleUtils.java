// Copyright 2002-2012, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.util;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;


/**
 * User: Sam Reid
 * Date: Feb 25, 2004
 * Time: 2:27:05 AM
 */
public class RectangleUtils {
    public static MutableVector2D getCenter( Rectangle2D rect ) {
        return new MutableVector2D( rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2 );
    }

}
