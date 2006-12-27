/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 3:29:34 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class VerticalSlit implements Potential {
    private Rectangle slit;
    private double barrierPotential;

    public VerticalSlit( Rectangle slit, double barrierPotential ) {
        this.slit = slit;
        this.barrierPotential = barrierPotential;
    }

    public double getPotential( int x, int y, int timestep ) {
        boolean inXRange = x < slit.getMaxX() && x > slit.getMinX();
        if( inXRange && !slit.contains( x, y ) ) {
            return barrierPotential;
        }
        return 0;
    }
}
