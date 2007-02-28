package edu.colorado.phet.bernoulli;

import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 30, 2003
 * Time: 1:20:51 AM
 * Copyright (c) Sep 30, 2003 by Sam Reid
 */
public class Flame extends SimpleObservable {
    private double x;
    private double y;
    private double width;
    private double height;

    public Flame( double x, double y, double width, double height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle2D.Double getRectangle() {
        return new Rectangle2D.Double( x, y, width, height );
    }

    public void reduceSize( double dx ) {
        this.width -= dx;
        this.height -= dx;
        this.x += dx / 2;
        updateObservers();
    }

    public void setState( double x, double y, double width, double height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateObservers();
    }
}
