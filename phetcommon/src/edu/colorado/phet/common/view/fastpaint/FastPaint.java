/**
 * Class: FastPaint
 * Package: edu.colorado.phet.common.view.fastpaint
 * Author: Another Guy
 * Date: Jun 22, 2004
 */
package edu.colorado.phet.common.view.fastpaint;

import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;

class FastPaint {
    private Component parent;
    private Rectangle lastBounds;
    private Graphic graphic;

    FastPaint( Component parent, Graphic graphic ) {
        this.parent = parent;
        this.graphic = graphic;
    }

    void repaint() {
        Rectangle viewBounds = graphic.getBounds();
        GraphicsUtil.fastRepaint( parent, lastBounds, viewBounds );
        lastBounds = viewBounds;
    }

    interface Graphic {
        Rectangle getBounds();
    }
}
