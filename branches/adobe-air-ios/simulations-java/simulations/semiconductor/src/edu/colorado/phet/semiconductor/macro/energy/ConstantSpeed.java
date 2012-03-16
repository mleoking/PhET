// Copyright 2002-2011, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.states.Speed;

/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 9:00:19 AM
 */
public class ConstantSpeed implements Speed {
    private double speed;

    public ConstantSpeed( double speed ) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }
}
