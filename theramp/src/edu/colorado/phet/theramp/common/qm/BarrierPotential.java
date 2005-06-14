/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 3:24:07 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class BarrierPotential implements Potential {
    private Rectangle rectangle;
    private double value;

    public BarrierPotential( Rectangle rectangle, double value ) {

        this.rectangle = rectangle;
        this.value = value;
    }

    public double getPotential( int x, int y, int timestep ) {
        if( rectangle.contains( x, y ) ) {
            return value;
        }
        else {
            return 0.0;
        }
    }
}
