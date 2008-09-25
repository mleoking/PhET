package edu.colorado.phet.semiconductor.phetcommon.view;

import java.awt.*;

/**
 * Operates on a Graphics2D object to prepare for rendering.
 */
public interface GraphicsSetup {
    /**
     * Applies this setup on the specified graphics object.
     *
     * @param graphics
     */
    void setup( Graphics2D graphics );
}
