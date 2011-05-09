// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.oldphetgraphics.graphics;

import java.awt.*;

/**
 * Represents an object that can be rendered on a Graphics2D.
 */
public interface Graphic {
    /**
     * Render this Graphic on a Graphics2D.
     *
     * @param g the Graphics2D on which to paint.
     */
    public void paint( Graphics2D g );
}
