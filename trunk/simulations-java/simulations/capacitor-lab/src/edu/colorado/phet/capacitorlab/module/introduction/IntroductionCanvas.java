/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.introduction;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;

/**
 * Canvas for the "Introduction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroductionCanvas extends CLCanvas {

    public IntroductionCanvas( CLModel model ) {
        
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
