package edu.colorado.phet.bernoulli.zoom;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 4:19:56 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class WallRep {
    double x;
    double y;
    double x2;
    double y2;

    public WallRep( Rectangle2D.Double rect ) {
        this.x = rect.x;
        this.y = rect.y;
        this.x2 = rect.x + rect.width;
        this.y2 = rect.y + rect.height;
    }

    public Rectangle2D.Double toRectangle() {
        return new Rectangle2D.Double( x, y, x2 - x, y2 - y );
    }

    public double getY() {
        return y;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public double getX() {
        return x;
    }

    public void move( double dx, double dy, double dx2, double dy2 ) {
        x += dx;
        y += dy;
        x2 += dx2;
        y2 += dy2;
    }
}
