/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;

/**
 * Canvas for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {

    public DielectricCanvas( CLModel model ) {
        
        BatteryNode batteryNode = new BatteryNode( model.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        addChild( batteryNode );
        
        CapacitorNode capacitorNode = new CapacitorNode( model.getCapacitor() );
        addChild( capacitorNode );
        capacitorNode.setOffset( 300, 0 );//XXX
    }
    
    public void reset() {
        //XXX
    }
}
