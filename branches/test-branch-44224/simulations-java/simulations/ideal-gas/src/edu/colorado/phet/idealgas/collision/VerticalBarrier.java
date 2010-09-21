/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.collision;

import java.awt.geom.Rectangle2D;

/**
 * VerticalBarrier
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VerticalBarrier extends Wall {
    private double minHeight;

    public VerticalBarrier( Rectangle2D bounds, Rectangle2D movementBounds ) {
        super( bounds, movementBounds );
    }

    public double getMinHeight() {
        return minHeight;
    }

    public void setMinHeight( double minHeight ) {
        this.minHeight = minHeight;
    }

    public void setBounds( Rectangle2D bounds ) {
        if( bounds.getHeight() >= minHeight ) {
            super.setBounds( bounds );
        }
    }
}
