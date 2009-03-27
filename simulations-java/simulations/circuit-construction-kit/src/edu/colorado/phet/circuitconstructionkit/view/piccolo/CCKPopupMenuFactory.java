package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.components.*;
import edu.colorado.phet.circuitconstructionkit.model.grabbag.GrabBagResistor;

/**
 * User: Sam Reid
 * Date: Oct 6, 2006
 * Time: 7:30:11 AM
 */

public class CCKPopupMenuFactory {
    private CCKModule module;

    public CCKPopupMenuFactory( CCKModule module ) {
        this.module = module;
    }

    public JPopupMenu createPopupMenu( Branch branch ) {
        if ( branch instanceof Switch ) {
            return new ComponentMenu.SwitchMenu( (Switch) branch, module );
        }
        else if ( branch instanceof SeriesAmmeter ) {
            return new ComponentMenu.SeriesAmmeterMenu( (SeriesAmmeter) branch, module );
        }
        else if ( branch instanceof ACVoltageSource ) {
            return new ComponentMenu.ACVoltageSourceMenu( (Battery) branch, module );
        }
        else if ( branch instanceof Battery ) {
            return new ComponentMenu.BatteryJMenu( (Battery) branch, module );
        }
        else if ( branch instanceof Bulb ) {
            return new ComponentMenu.BulbMenu( (Bulb) branch, module );
        }
        else if ( branch instanceof Capacitor ) {
            return new ComponentMenu.CapacitorMenu( (Capacitor) branch, module );
        }
        else if ( branch instanceof GrabBagResistor ) {
            return new ComponentMenu.GrabBagResistorMenu( (GrabBagResistor) branch, module );
        }
        else if ( branch instanceof Inductor ) {
            return new ComponentMenu.InductorMenu( (Inductor) branch, module );
        }
        else if ( branch instanceof Resistor ) {
            return new ComponentMenu.ResistorMenu( (Resistor) branch, module );
        }
        else if ( branch instanceof Wire ) {
            return new WirePopupMenu( module.getCCKModel(), branch );
        }
        else {
            System.out.println( "No popup menu found for component: " + branch );
            return null;
        }
    }
}
