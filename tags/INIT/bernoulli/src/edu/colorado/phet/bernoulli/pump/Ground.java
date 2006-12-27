package edu.colorado.phet.bernoulli.pump;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 1:01:43 PM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class Ground {
    double x;
    double y;
    double width;
    double height;
    Rectangle2D.Double rect;

    public Ground( double x, double y, double width, double height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        rect = new Rectangle2D.Double( x, y, width, height );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean contains( double x, double y ) {
        return rect.contains( x, y );
    }
}
