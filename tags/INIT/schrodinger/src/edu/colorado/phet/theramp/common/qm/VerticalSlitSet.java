/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 3:29:34 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class VerticalSlitSet implements Potential {
    private Rectangle[] slits;
    private double barrierPotential;

    public VerticalSlitSet( Rectangle[] slits, double barrierPotential ) {
        this.slits = slits;
        this.barrierPotential = barrierPotential;
    }

    public double getPotential( int x, int y, int timestep ) {
//        for (int i=0;i<slits.length;i++){
//            if (slits[i].contains( x,y)){
//                return 0;
//            }
//        }
//
//        boolean inXRange = x < slits.getMaxX() && x > slits.getMinX();
//        if( inXRange && !slits.contains( x, y ) ) {
//            return barrierPotential;
//        }
        return 0;
    }
}
