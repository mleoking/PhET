/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;

/**
 * Canvas for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {
    
    private final BatteryNode batteryNode;
    private final CapacitorNode capacitorNode;
    private final ModelViewTransform mvt;
    
    public DielectricCanvas( CLModel model ) {
        
        mvt = new ModelViewTransform( CLConstants.MVT_SCALE, CLConstants.MVT_OFFSET );
        
        batteryNode = new BatteryNode( model.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        addChild( batteryNode );
        
        capacitorNode = new CapacitorNode( model.getCapacitor(), mvt );
        addChild( capacitorNode );
        capacitorNode.setOffset( 300, 0 );//XXX
    }
    
    public void reset() {
        //XXX
    }
}
