package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Apr 27, 2004
 * Time: 10:17:41 PM
 */
public class Magnet {
    private Rectangle2D.Double bounds;

    public Magnet( Rectangle2D.Double bounds ) {
        this.bounds = bounds;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void translate( double x, double y ) {
        bounds.x += x;
        bounds.y += y;
    }

    public Shape getTranslatedShape( double x, double y ) {
        return new Rectangle2D.Double( bounds.x + x, bounds.y + y, bounds.width, bounds.height );
    }

    public PhetVector getPlusSide() {
        return new PhetVector( bounds.x, bounds.y );
    }
}
