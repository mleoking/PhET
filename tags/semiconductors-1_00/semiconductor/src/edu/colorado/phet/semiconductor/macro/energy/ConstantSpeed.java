/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.states.Speed;

/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 9:00:19 AM
 * Copyright (c) Mar 17, 2004 by Sam Reid
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
