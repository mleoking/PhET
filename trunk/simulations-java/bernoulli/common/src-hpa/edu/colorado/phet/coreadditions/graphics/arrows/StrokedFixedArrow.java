/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics.arrows;

import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 9:55:59 AM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class StrokedFixedArrow {
    private int headHeight;
    Arrowhead head;
    LineSegment segment;

    public StrokedFixedArrow(Color color, int tailWidth) {
        this(color, color, new BasicStroke(tailWidth), tailWidth * 3, tailWidth * 3);
    }

    public StrokedFixedArrow(Color tailColor, Color headColor, Stroke tailStroke, int headWidth, int headHeight) {
        this.headHeight = headHeight;
        this.head = new Arrowhead(headColor, headWidth, headHeight);
        segment = new LineSegment(tailStroke, tailColor);
    }

    public void drawLine(Graphics2D g2, int x1, int y1, int x2, int y2) {
        PhetVector vector = new PhetVector(x2 - x1, y2 - y1);
        double mag = vector.getMagnitude();
        if (mag <= headHeight)
            return;
        vector.setMagnitude(vector.getMagnitude() - headHeight);
        Point headPoint = new Point((int) vector.getX() + x1, (int) vector.getY() + y1);
        segment.paint(g2, x1, y1, headPoint.x, headPoint.y);
        head.paint(g2, headPoint.x, headPoint.y, (int) vector.getX(), (int) vector.getY());
    }

    public boolean headContains(Point point) {
        return head.contains(point);
    }

    public boolean tailContains(Point point) {
        return !headContains(point) && segment.contains(point);
    }

    public boolean contains(Point point) {
        return headContains(point) || segment.contains(point);
    }
}
