package edu.colorado.phet.common_1200.view;

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
