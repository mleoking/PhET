/*, 2003.*/
package edu.colorado.phet.semiconductor.util;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.semiconductor.util.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Feb 25, 2004
 * Time: 2:27:05 AM
 */
public class RectangleUtils {
    public static PhetVector getCenter( Rectangle2D rect ) {
        return new PhetVector( rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2 );
    }

}
