/*, 2003.*/
package edu.colorado.phet.semiconductor_semi.macro.energy;

import edu.colorado.phet.common_semiconductor.math.PhetVector;
import edu.colorado.phet.semiconductor_semi.macro.circuit.battery.Battery;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 9:47:48 AM
 */
public class ElectricFieldSection {
    ElectricField internalField;
    ElectricField batteryField;

    public ElectricFieldSection( PhetVector center ) {

        internalField = new ElectricField( center.getAddedInstance( 0, .3 ) );
        batteryField = new ElectricField( center.getAddedInstance( 0, -.3 ) );
    }

    public ElectricField getInternalField() {
        return internalField;
    }

    public ElectricField getBatteryField() {
        return batteryField;
    }

    public static double toVoltStrength( double input ) {
        return input;
    }

    public void voltageChanged( Battery source ) {
        batteryField.setStrength( toVoltStrength( source.getVoltage() ) );
    }

}
