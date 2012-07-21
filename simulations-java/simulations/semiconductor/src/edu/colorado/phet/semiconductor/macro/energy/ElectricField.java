// Copyright 2002-2012, University of Colorado

/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;


/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 9:59:15 AM
 */
public class ElectricField {
    private Vector2D center;
    double strength = 0;

    public ElectricField( Vector2D center ) {
        this.center = center;
    }

    public double getStrength() {
        return strength;
    }

    public Vector2D getCenter() {
        return center;
    }

    public void setStrength( double strength ) {
        this.strength = strength;
    }
}
