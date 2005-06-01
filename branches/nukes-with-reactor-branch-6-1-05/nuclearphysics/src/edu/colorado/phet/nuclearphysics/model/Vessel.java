/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Rectangle2D;
import java.awt.*;

/**
 * Vessel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Vessel {
    private Rectangle2D rep;

    public Vessel( double x, double y, double width, double height ) {

        rep = new Rectangle2D.Double( x, y, width, height );
    }

    public Shape getShape() {
        return rep;
    }

    public double getX() {
        return rep.getX();
    }

    public double getY() {
        return rep.getY();
    }

    public double getWidth() {
        return rep.getWidth();
    }

    public double getHeight() {
        return rep.getHeight();
    }
}
