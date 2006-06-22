/**
 * Class: FastPaint
 * Package: edu.colorado.phet.common.view.fastpaint
 * Author: Another Guy
 * Date: Jun 22, 2004
 */
package edu.colorado.phet.common_cck.view.fastpaint;

import javax.swing.*;
import java.awt.*;

public class FastPaint {
    private Component parent;
    private Rectangle lastBounds;
    private Graphic graphic;

    FastPaint( Component parent, Graphic graphic ) {
        this.parent = parent;
        this.graphic = graphic;
    }

    void repaint() {
        Rectangle viewBounds = graphic.getBounds();
        fastRepaint( parent, lastBounds, viewBounds );
        lastBounds = viewBounds;
    }

    public static void fastRepaint( final Component parent, final Rectangle bounds ) {
        boolean dolater = false;
        //        boolean dolater=true;
        if( dolater ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    if( bounds != null ) {
                        JComponent jc = (JComponent)parent;
                        jc.paintImmediately( bounds );
                    }
                }
            } );
        }
        else {
            if( bounds != null ) {
                parent.repaint( bounds.x, bounds.y, bounds.width, bounds.height );
            }
        }
    }

    public static void fastRepaint( Component parent, Rectangle orig, Rectangle newRect ) {
        fastRepaint( parent, orig );
        fastRepaint( parent, newRect );
    }

    interface Graphic {
        Rectangle getBounds();
    }
}
