/** Sam Reid*/
package edu.colorado.phet.common.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 29, 2004
 * Time: 3:45:00 PM
 * Copyright (c) Aug 29, 2004 by Sam Reid
 */
public class GraphicsRestore {
    private GraphicsState graphicsState = new GraphicsState();
    private Graphics2D graphics2D;

    public GraphicsRestore( Graphics2D graphics2D ) {
        this.graphics2D = graphics2D;
        graphicsState.saveState( graphics2D );
    }

    public void restore() {
        graphicsState.restoreState( graphics2D );
    }
}
