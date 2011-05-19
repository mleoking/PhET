// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CurrentIndicatorNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Base class for all circuit nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AbstractCircuitNode extends PhetPNode {

    private final BatteryNode batteryNode;
    private final CurrentIndicatorNode topCurrentIndicatorNode, bottomCurrentIndicatorNode;

    public AbstractCircuitNode( ICircuit circuit, CLModelViewTransform3D mvt ) {

        // battery, at model location
        Battery battery = circuit.getBattery();
        batteryNode = new BatteryNode( battery, CLConstants.BATTERY_VOLTAGE_RANGE );
        addChild( batteryNode );
        Point2D pView = mvt.modelToView( battery.getLocationReference() );
        batteryNode.setOffset( pView );

        // current indicators
        topCurrentIndicatorNode = new CurrentIndicatorNode( circuit, 0 );
        topCurrentIndicatorNode.scale( 0.75 );
        addChild( topCurrentIndicatorNode );
        bottomCurrentIndicatorNode = new CurrentIndicatorNode( circuit, Math.PI );
        bottomCurrentIndicatorNode.scale( 0.75 );
        addChild( bottomCurrentIndicatorNode );

        //TODO temporary locations, until we have wires
        topCurrentIndicatorNode.setOffset( batteryNode.getFullBoundsReference().getMaxX(), batteryNode.getFullBounds().getMinY() - 50 );
        bottomCurrentIndicatorNode.setOffset( topCurrentIndicatorNode.getXOffset(), batteryNode.getFullBounds().getMaxY() + 50 );
    }

    protected BatteryNode getBatteryNode() {
        return batteryNode;
    }

    protected CurrentIndicatorNode getTopCurrentIndicatorNode() {
        return topCurrentIndicatorNode;
    }

    protected CurrentIndicatorNode getBottomCurrentIndicatorNode() {
        return bottomCurrentIndicatorNode;
    }
}
