package edu.colorado.phet.coreadditions.graphics;

import java.awt.*;

public class DifferentialDragHandler {

    private Point dragStartPt;

    public DifferentialDragHandler(Point mouseStart) {
        this.dragStartPt = mouseStart;
    }

    public Point getDifferentialLocation(Point p) {
        int dx = p.x - dragStartPt.x;
        int dy = p.y - dragStartPt.y;
        Point at = new Point(dx, dy);
        return at;
    }

    public Point getDifferentialLocationAndReset(Point point) {
        Point p = getDifferentialLocation(point);
        this.dragStartPt.x = point.x;
        this.dragStartPt.y = point.y;
        return p;
    }

}
