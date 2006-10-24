/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 10:20:35 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */

public class Lattice2D {
    Point2D.Double[][] lattice;
    private int width;
    private int height;

    public Lattice2D( int width, int height ) {
        lattice = new Point2D.Double[width][height];
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setValue( int i, int k, double x, double y ) {
        lattice[i][k] = new Point2D.Double( x, y );
    }
}
