/**
 * Class: InteractiveGraphic
 * Package: edu.colorado.phet.common.view.graphics.paint
 * Author: Another Guy
 * Date: May 21, 2003
 */
package edu.colorado.phet.microwaves.common.graphics;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public interface InteractiveGraphic extends Graphic {

//    boolean canHandleMousePress(MouseEvent event);
//
//    void mousePressed(MouseEvent event);
//
//    void mouseDragged(MouseEvent event);
//
//    void mouseReleased(MouseEvent event);
//
//    void mouseEntered(MouseEvent event);
//
//    void mouseExited(MouseEvent event);

    boolean canHandleMousePress( MouseEvent event, Point2D.Double modelLoc );

    void mouseDragged( MouseEvent event, Point2D.Double modelLoc );

    void mousePressed( MouseEvent event, Point2D.Double modelLoc );

    void mouseReleased( MouseEvent event, Point2D.Double modelLoc );

    void mouseEntered( MouseEvent event, Point2D.Double modelLoc );

    void mouseExited( MouseEvent event, Point2D.Double modelLoc );
}
