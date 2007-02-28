package edu.colorado.phet.bernoulli.valves;

import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 1:12:45 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class Valve extends SimpleObservable {
    boolean open;
    double x;
    double y;
    double height;
    double width;

    public double getWidth() {
        return width;
    }

    public Valve( double x, double y, double width, double height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isOpen() {
        return open;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setOpen( boolean b ) {
        this.open = b;
        updateObservers();
    }

    public double getHeight() {
        return height;
    }

    public void setY( double y ) {
        this.y = y;
        updateObservers();
    }

}
