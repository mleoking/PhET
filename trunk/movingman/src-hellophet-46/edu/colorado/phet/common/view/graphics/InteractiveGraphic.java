/**
 * Class: InteractiveGraphic
 * Package: edu.colorado.phet.common.view.graphics.repaint
 * Author: Another Guy
 * Date: May 21, 2003
 */
package edu.colorado.phet.common.view.graphics;

import java.awt.event.MouseEvent;

public interface InteractiveGraphic extends Graphic {
    boolean canHandleMousePress( MouseEvent event );

    void mousePressed( MouseEvent event );

    void mouseDragged( MouseEvent event );

    void mouseReleased( MouseEvent event );

    void mouseEntered( MouseEvent event );

    void mouseExited( MouseEvent event );
}
