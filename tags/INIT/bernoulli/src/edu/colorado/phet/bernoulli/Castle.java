package edu.colorado.phet.bernoulli;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 30, 2003
 * Time: 12:57:31 AM
 * Copyright (c) Sep 30, 2003 by Sam Reid
 */
public class Castle {
    double x;
    double y;
    double width;
    double height;

    public Castle( double x, double y, double width, double height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle2D.Double getRectangle() {
        return new Rectangle2D.Double( x, y, width, height );
    }

    public double getX() {
        return x;
    }

    public double getWidth() {
        return width;
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

//    public Flame newFlame() {
//
//        Flame f=new Flame();
//    }
}
