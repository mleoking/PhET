/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics.positioned;

import edu.colorado.phet.common.view.graphics.positioned.PositionedGraphic;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Mar 1, 2003
 * Time: 6:14:53 PM
 * To change this template use Options | File Templates.
 */
public class PositionedOvalGraphic implements PositionedGraphic {
    int width;
    int height;
    Color c;

    public PositionedOvalGraphic(int width, int height, Color c) {
        this.width = width;
        this.height = height;
        this.c = c;
    }

    public void paint(Graphics2D g, int x, int y) {
        int left = x - width / 2;
        int top = y - height / 2;
        g.setColor(c);
        g.fillOval(left, top, width, height);
    }

    public boolean containsPoint(int xCircle, int yCircle, int x, int y) {
        double radius = (width + height) / 2;
        double dist = new Point2D.Double(x - xCircle, y - yCircle).distance(0, 0);
        return dist < radius;
    }
}
