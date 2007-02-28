/**
 * Class: DragHandler
 * Package: edu.colorado.phet.common.view.graphics.paint
 * Author: Another Guy
 * Date: May 22, 2003
 */
package edu.colorado.phet.common.view.graphics;

import java.awt.*;

public class DragHandler {

    private Point dragStartPt;
    private Point viewStart;
    private Point newLocation = new Point();

    public DragHandler( Point mouseStart, Point viewStart ) {
        this.dragStartPt = mouseStart;
        this.viewStart = viewStart;
    }

    public Point getNewLocation( Point p ) {
        int dx = p.x - dragStartPt.x;
        int dy = p.y - dragStartPt.y;
        newLocation.x = dx + viewStart.x;
        newLocation.y = dy + viewStart.y;
        return newLocation;
    }

}
