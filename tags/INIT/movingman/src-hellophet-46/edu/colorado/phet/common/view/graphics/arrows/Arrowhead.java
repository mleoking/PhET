/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics.arrows;

import edu.colorado.phet.common.math.PhetVector;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 9:58:05 AM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class Arrowhead {
    Color c;
    int width;
    int height;
    Polygon poly = new Polygon();

    public Arrowhead(Color c, int width, int height) {
        this.c = c;
        this.width = width;
        this.height = height;
    }

    public void paint(Graphics2D g, int x0, int y0, int xdir, int ydir) {
        paint(g, new PhetVector(x0, y0), new PhetVector(xdir, ydir));
    }

    public void paint(Graphics2D g, PhetVector root, PhetVector direction) {
        poly.reset();
        direction.normalize();

        PhetVector normal = direction.getNormalVector();
        PhetVector lefty = root.getAddedInstance(normal.getScaledInstance(width / 2));
        poly.addPoint((int) lefty.getX(), (int) lefty.getY());
        PhetVector tippy = root.getAddedInstance(direction.getScaledInstance(height));
        poly.addPoint((int) tippy.getX(), (int) tippy.getY());
        PhetVector righty = root.getAddedInstance(normal.getScaledInstance(-width / 2));
        poly.addPoint((int) righty.getX(), (int) righty.getY());

        g.setColor(c);
        g.fill(poly);
    }

}
