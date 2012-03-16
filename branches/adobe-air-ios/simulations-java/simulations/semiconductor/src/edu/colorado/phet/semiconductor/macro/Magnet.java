// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.macro;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;


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

    public Vector2D getPlusSide() {
        return new Vector2D( bounds.x, bounds.y );
    }
}
