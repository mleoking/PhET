// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Base class for all circuit nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AbstractCircuitNode extends PhetPNode {

    private final BatteryNode batteryNode;

    public AbstractCircuitNode( ICircuit circuit, CLModelViewTransform3D mvt ) {

        Battery battery = circuit.getBattery();
        batteryNode = new BatteryNode( battery, CLConstants.BATTERY_VOLTAGE_RANGE );
        addChild( batteryNode );
        // battery at model location
        Point2D pView = mvt.modelToView( battery.getLocationReference() );
        batteryNode.setOffset( pView );
    }

    protected BatteryNode getBatteryNode() {
        return batteryNode;
    }
}
