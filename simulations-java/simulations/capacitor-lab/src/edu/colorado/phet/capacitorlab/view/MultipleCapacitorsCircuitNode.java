// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view;

import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.control.CapacitanceControlNode;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;

/**
 * Circuit node used in the "Multiple Capacitors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsCircuitNode extends PhetPNode {

    public MultipleCapacitorsCircuitNode( ICircuit circuit, CLModelViewTransform3D mvt,
                                          Property<Boolean> plateChargeVisible, final Property<Boolean> eFieldVisible, Property<DielectricChargeView> dielectricChargeView,
                                          double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {

        // battery
        Battery battery = circuit.getBattery();
        BatteryNode batteryNode = new BatteryNode( battery, CLConstants.BATTERY_VOLTAGE_RANGE );
        addChild( batteryNode );
        batteryNode.setOffset( mvt.modelToView( battery.getLocationReference() ) );

        // capacitors
        for ( Capacitor capacitor : circuit.getCapacitors() ) {

            // capacitor
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

        // wires
        ArrayList<Wire> wires = circuit.getWires();
        WireNode topWireNode = null;
        WireNode bottomWireNode = null;
        for ( int i = wires.size() - 1; i >= 0; i-- ) {

            WireNode wireNode = new WireNode( wires.get( i ) );
            addChild( wireNode );

            if ( i == wires.size() - 1 ) {
                bottomWireNode = wireNode;
            }
            else if ( i == 0 ) {
                topWireNode = wireNode;
            }
        }

        // top current indicator, centered on top wire, between battery and first capacitor to the right of the battery
        CurrentIndicatorNode topCurrentIndicatorNode = new CurrentIndicatorNode( circuit, 0 );
        {
            topCurrentIndicatorNode.scale( 0.75 );
            addChild( topCurrentIndicatorNode );
            double topWireThickness = mvt.modelToViewDelta( 0, topWireNode.getWire().getThickness(), 0 ).getY();
            double xModel = circuit.getBattery().getX() + ( ( circuit.getCapacitors().get( 0 ).getX() - circuit.getBattery().getX() ) / 2 );
            double x = mvt.modelToViewDelta( xModel, 0, 0 ).getX();
            double y = topWireNode.getFullBoundsReference().getMinY() + ( topWireThickness / 2 );
            topCurrentIndicatorNode.setOffset( x, y );
        }

        // bottom current indicator, centered on bottom wire, between battery and first capacitor to the right of the battery
        CurrentIndicatorNode bottomCurrentIndicatorNode = new CurrentIndicatorNode( circuit, Math.PI );
        {
            bottomCurrentIndicatorNode.scale( 0.75 );
            addChild( bottomCurrentIndicatorNode );
            double bottomWireThickness = mvt.modelToViewDelta( 0, bottomWireNode.getWire().getThickness(), 0 ).getY();
            double x = topCurrentIndicatorNode.getXOffset();
            double y = bottomWireNode.getFullBoundsReference().getMaxY() - ( bottomWireThickness / 2 );
            bottomCurrentIndicatorNode.setOffset( x, y );
        }
    }
}
