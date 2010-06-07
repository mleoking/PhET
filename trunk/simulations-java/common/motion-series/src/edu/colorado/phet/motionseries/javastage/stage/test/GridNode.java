package edu.colorado.phet.motionseries.javastage.stage.test;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Line2D;

public class GridNode extends PNode {
    double x0;
    double y0;
    double dx;
    double dy;
    int nx;
    int ny;
    private float strokeWidth;

    GridNode(float strokeWidth, double x0, double y0, double dx, double dy, int nx, int ny) {
        this.strokeWidth = strokeWidth;
        this.x0 = x0;
        this.y0 = y0;
        this.dx = dx;
        this.dy = dy;
        this.nx = nx;
        this.ny = ny;

        for (int i = 0; i < nx + 1; i++) {//the +1 puts the end caps on the gridlines
            double x = x0 + i * dx;
            PhetPPath path = new PhetPPath(new Line2D.Double(x, y0, x, y0 + ny * dy), new BasicStroke(getStrokeWidth(i)), getColor(i));
            addChild(path);
        }
        for (int k = 0; k < ny + 1; k++) {
            double y = y0 + k * dy;
            PhetPPath path = new PhetPPath(new Line2D.Double(x0, y, x0 + nx * dx, y), new BasicStroke(getStrokeWidth(k)), getColor(k));
            addChild(path);
        }
    }

    private float getStrokeWidth(int k) {
        if (k % 10 == 0) return strokeWidth * 3;
        else if (k % 5 == 0) return strokeWidth * 2;
        return strokeWidth;
    }

    //Shows every 5th line as

    private Color getColor(int k) {
        if (k % 10 == 0) return Color.black;
        else if (k % 5 == 0) return Color.gray;
        else return Color.lightGray;
    }

    /**
     * Creates a grid that is symmetrical in x-y
     */
    GridNode(float strokeWidth, double x0, double dx, int nx) {
        this(strokeWidth, x0, x0, dx, dx, nx, nx);
    }
}
