/**
 * Class: DragHandler
 * Package: edu.colorado.phet.common.view.graphics.paint
 * Author: Another Guy
 * Date: May 22, 2003
 */
package edu.colorado.phet.common.view.graphics;

import java.awt.*;
import java.awt.geom.Point2D;

public class DragHandler {

    private Point2D.Double dragStartPt;
    private Point2D.Double viewStart;
    private Point2D.Double newLocation = new Point2D.Double();

    public DragHandler(Point2D.Double mouseStart, Point2D.Double viewStart) {
        this.dragStartPt = mouseStart;
        this.viewStart = viewStart;
    }


    public Point2D.Double getNewLocation(Point2D.Double p) {
        double dx = p.x - dragStartPt.x;
        double dy = p.y - dragStartPt.y;
        newLocation.x = dx + viewStart.x;
        newLocation.y = dy + viewStart.y;
        return newLocation;
    }

}
