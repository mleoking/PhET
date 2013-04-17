// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.control.CapacitanceControlNode;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;

/**
 * Circuit node used in the "Multiple Capacitors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsCircuitNode extends PhetPNode {

    private final ICircuit circuit;

    public MultipleCapacitorsCircuitNode( ICircuit circuit, CLModelViewTransform3D mvt, boolean dielectricVisible,
                                          Property<Boolean> plateChargeVisible, final Property<Boolean> eFieldVisible, Property<DielectricChargeView> dielectricChargeView,
                                          double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {

        this.circuit = circuit;

        // battery
        Battery battery = circuit.getBattery();
        BatteryNode batteryNode = new BatteryNode( battery, CLConstants.BATTERY_VOLTAGE_RANGE );
        addChild( batteryNode );
        batteryNode.setOffset( mvt.modelToView( battery.getLocationReference() ) );

        // capacitors
        int subscript = 1; // capacitor numbering visible to user starts at 1
        for ( Capacitor capacitor : circuit.getCapacitors() ) {

            // capacitor
            final CapacitorNode capacitorNode = new CapacitorNode( capacitor, mvt, dielectricVisible,
                                                                   plateChargeVisible, eFieldVisible, dielectricChargeView,
                                                                   maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
            capacitorNode.getDielectricNode().setVisible( false );
            addChild( capacitorNode );
            capacitorNode.setOffset( mvt.modelToView( capacitor.getLocation() ) );

            // label, above left of capacitor
            String html = MessageFormat.format( CLStrings.PATTERN_CAPACITOR_NUMBER, subscript );
            final HTMLNode labelNode = new HTMLNode( html );
            labelNode.setFont( new PhetFont( 24 ) );
            subscript++;
            addChild( labelNode );
            capacitorNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                // When capacitor bounds change (due to plate separation change), move the label.
                public void propertyChange( PropertyChangeEvent event ) {
                    updateLabelOffset( labelNode, capacitorNode );
                }
            } );
            updateLabelOffset( labelNode, capacitorNode );

            // capacitance control, to the left of the capacitor, tucked slightly under bottom plate
            CapacitanceControlNode capacitanceControlNode = new CapacitanceControlNode( capacitor,
                                                                                        CLConstants.CAPACITANCE_RANGE,
                                                                                        CLConstants.CAPACITANCE_CONTROL_EXPONENT );
            addChild( capacitanceControlNode );
            double x = capacitorNode.getFullBoundsReference().getMinX() - capacitanceControlNode.getFullBoundsReference().getWidth() - PNodeLayoutUtils.getOriginXOffset( capacitanceControlNode ) + 20;
            double y = capacitorNode.getYOffset() - ( capacitanceControlNode.getFullBoundsReference().getHeight() / 2 ) + 20;
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

    // This method must be called if the model element has a longer lifetime than the view.
    public void cleanup() {
        //FUTURE call cleanup methods of component nodes
    }

    public ICircuit getCircuit() {
        return circuit;
    }

    private static void updateLabelOffset( PNode labelNode, CapacitorNode capacitorNode ) {
        double x = capacitorNode.getFullBoundsReference().getMinX();
        double y = capacitorNode.getFullBoundsReference().getMinY() - labelNode.getFullBoundsReference().getHeight() + 5;
        labelNode.setOffset( x, y );
    }
}
