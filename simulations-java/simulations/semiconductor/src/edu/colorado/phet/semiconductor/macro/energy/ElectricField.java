/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;


/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 9:59:15 AM
 */
public class ElectricField {
    private AbstractVector2D center;
    double strength = 0;

    public ElectricField( AbstractVector2D center ) {
        this.center = center;
    }

    public double getStrength() {
        return strength;
    }

    public AbstractVector2D getCenter() {
        return center;
    }

    public void setStrength( double strength ) {
        this.strength = strength;
    }
}
