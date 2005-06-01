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
import java.awt.geom.Point2D;
import java.awt.*;

/**
 * ControlRod
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlRod {
    private Rectangle2D.Double rep;

    public ControlRod( Point2D p1, Point2D p2, double thickness ) {
        // Is the rod horizontal?
        if( p1.getY() == p2.getY() ) {
            rep = new Rectangle2D.Double( p1.getX(), p1.getY() - thickness / 2,
                                          p2.getX() - p1.getX(), thickness );
        }
        // Is the rod vertical?
        if( p1.getX() == p2.getX() ) {
            rep = new Rectangle2D.Double( p1.getX() - thickness / 2, p1.getY(),
                                          thickness, p2.getY() - p1.getY() );
        }
    }

    public Shape getShape() {
        return rep;
    }
}
