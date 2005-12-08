/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.mousecontrol;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:44:00 AM
 * Copyright (c) Oct 9, 2003 by Sam Reid
 */
public interface MouseControl {
    public boolean canHandleMousePress(MouseEvent event, Point2D.Double modelLoc);

    public void mouseDragged(MouseEvent event, Point2D.Double modelLoc);

    public void mousePressed(MouseEvent event, Point2D.Double modelLoc);

    public void mouseReleased(MouseEvent event, Point2D.Double modelLoc);

    public void mouseEntered(MouseEvent event, Point2D.Double modelLoc);

    public void mouseExited(MouseEvent event, Point2D.Double modelLoc);
}
