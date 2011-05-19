// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.control.CapacitanceControlNode;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.CurrentIndicatorNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;

/**
 * Base class for all circuit nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AbstractCircuitNode extends PhetPNode {

    private final BatteryNode batteryNode;
    private final CurrentIndicatorNode topCurrentIndicatorNode, bottomCurrentIndicatorNode;

    public AbstractCircuitNode( ICircuit circuit, CLModelViewTransform3D mvt,
                                Property<Boolean> plateChargeVisible, final Property<Boolean> eFieldVisible, Property<DielectricChargeView> dielectricChargeView,
                                double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {

        // battery, at model location
        Battery battery = circuit.getBattery();
        batteryNode = new BatteryNode( battery, CLConstants.BATTERY_VOLTAGE_RANGE );
        addChild( batteryNode );
        batteryNode.setOffset( mvt.modelToView( battery.getLocationReference() ) );

        // capacitors
        for ( Capacitor capacitor : circuit.getCapacitors() ) {

            // capacitor node, at its model location
            CapacitorNode capacitorNode = new CapacitorNode( capacitor, mvt,
                                                             plateChargeVisible, eFieldVisible, dielectricChargeView,
                                                             maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
            capacitorNode.getDielectricNode().setVisible( false );
            addChild( capacitorNode );
            capacitorNode.setOffset( mvt.modelToView( capacitor.getLocation() ) );

            // capacitance control, to the left of the capacitor
            CapacitanceControlNode capacitanceControlNode = new CapacitanceControlNode( capacitor,
                                                                                        MultipleCapacitorsModel.CAPACITANCE_RANGE, MultipleCapacitorsModel.CAPACITANCE_DISPLAY_EXPONENT );
            addChild( capacitanceControlNode );
            double x = capacitorNode.getFullBoundsReference().getMinX() - capacitanceControlNode.getFullBoundsReference().getWidth() - PNodeLayoutUtils.getOriginXOffset( capacitanceControlNode ) - 5;
            double y = capacitorNode.getYOffset() - ( capacitanceControlNode.getFullBoundsReference().getHeight() / 2 );
            capacitanceControlNode.setOffset( x, y );
        }

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
