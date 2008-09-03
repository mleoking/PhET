/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 9:59:15 AM
 */
public class ElectricField {
    private PhetVector center;
    double strength = 0;

    public ElectricField( PhetVector center ) {
        this.center = center;
    }

    public double getStrength() {
        return strength;
    }

    public PhetVector getCenter() {
        return center;
    }

    public void setStrength( double strength ) {
        this.strength = strength;
    }
}
