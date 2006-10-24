/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 9:30:26 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class RectanglePotential extends AtomPotential {

    public RectanglePotential( Point center, int diameter, double potentialValue ) {
        super( center, diameter, potentialValue );
    }

    protected boolean inRange( Point testPoint ) {
        Rectangle2D rect = getRect();
        return rect.contains( testPoint );
    }

    private Rectangle2D getRect() {
        int width = getDiameter();
        return new Rectangle2D.Double( getCenter().getX() - width / 2, getCenter().getY() - width / 2, width, width );
    }

    public String toString() {
        return super.toString() + ", " + getRect();
    }
}
