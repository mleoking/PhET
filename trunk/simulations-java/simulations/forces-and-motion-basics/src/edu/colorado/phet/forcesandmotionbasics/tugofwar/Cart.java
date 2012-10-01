// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;

/**
 * The cart in the center of the screen that each team will try to pull across the center line indicated by the Caret.
 *
 * @author Sam Reid
 */
class Cart {
    public final double weight = 1000;       //in newtons, weighs a little more than a big puller
    private static final boolean dev = false;
    private static final double DEBUG_SPEED = dev ? 5 : 1;
    private double velocity = 0;
    public final DoubleProperty position = new DoubleProperty( 0.0 );

    public double getPosition() { return position.get(); }

    public void stepInTime( double dt, double acceleration ) {
        dt = dt * 35 * DEBUG_SPEED;//speed up time because the masses of the characters give everything too much momentum
        velocity = velocity + acceleration * dt;
        position.set( position.get() + velocity * dt );
    }

    public void restart() {
        velocity = 0.0;
        position.reset();
    }
}