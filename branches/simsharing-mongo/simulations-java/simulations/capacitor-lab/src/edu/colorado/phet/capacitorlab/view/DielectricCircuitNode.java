// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.Cursor;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.control.BatteryConnectionButtonNode;
import edu.colorado.phet.capacitorlab.control.PlateChargeControlNode;
import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandler;
import edu.colorado.phet.capacitorlab.drag.PlateAreaDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.PlateSeparationDragHandleNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit.CircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.circuit.SingleCircuit;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;

/**
 * Circuit for the for the "Dielectric" module.
 * Contains the circuit components and controls for manipulating the circuit.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCircuitNode extends PhetPNode {

    private final SingleCircuit circuit;
    private final CapacitorNode capacitorNode;
    private final WireNode topWireNode, bottomWireNode;
    private final CurrentIndicatorNode topCurrentIndicatorNode, bottomCurrentIndicatorNode;
    private final PlateChargeControlNode plateChargeControlNode;

    public DielectricCircuitNode( final SingleCircuit circuit, final CLModelViewTransform3D mvt, final boolean dielectricVisible,
                                  Property<Boolean> plateChargeVisibleProperty, final Property<Boolean> eFieldVisibleProperty,
                                  Property<DielectricChargeView> dielectricChargeView,
                                  double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {

        this.circuit = circuit;

        // circuit components
        BatteryNode batteryNode = new BatteryNode( circuit.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        capacitorNode = new CapacitorNode( circuit.getCapacitor(), mvt, dielectricVisible,
                                           plateChargeVisibleProperty, eFieldVisibleProperty, dielectricChargeView,
                                           maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField ) {{
            if ( dielectricVisible ) {
                // make dielectric directly draggable
                getDielectricNode().addInputEventListener( new CursorHandler( Cursor.E_RESIZE_CURSOR ) );
                getDielectricNode().addInputEventListener( new DielectricOffsetDragHandler( this, circuit.getCapacitor(), mvt, CLConstants.DIELECTRIC_OFFSET_RANGE ) );
            }
            else {
                getDielectricNode().setVisible( false );
            }
        }};
        topWireNode = new WireNode( circuit.getTopWire() );
        bottomWireNode = new WireNode( circuit.getBottomWire() );

        // drag handles
        DielectricOffsetDragHandleNode dielectricOffsetDragHandleNode = new DielectricOffsetDragHandleNode( circuit.getCapacitor(), mvt, CLConstants.DIELECTRIC_OFFSET_RANGE );
        PlateSeparationDragHandleNode plateSeparationDragHandleNode = new PlateSeparationDragHandleNode( circuit.getCapacitor(), mvt, CLConstants.PLATE_SEPARATION_RANGE );
        PlateAreaDragHandleNode plateAreaDragHandleNode = new PlateAreaDragHandleNode( circuit.getCapacitor(), mvt, CLConstants.PLATE_WIDTH_RANGE );

        // current indicators
        topCurrentIndicatorNode = new CurrentIndicatorNode( circuit, 0 );
        bottomCurrentIndicatorNode = new CurrentIndicatorNode( circuit, Math.PI );

        // controls
        BatteryConnectionButtonNode batteryConnectionButtonNode = new BatteryConnectionButtonNode( circuit );
        plateChargeControlNode = new PlateChargeControlNode( circuit, new DoubleRange( -maxPlateCharge, maxPlateCharge ) );

        // rendering order
        addChild( bottomWireNode );
        addChild( batteryNode );
        addChild( capacitorNode );
        addChild( topWireNode );
        addChild( topCurrentIndicatorNode );
        addChild( bottomCurrentIndicatorNode );
        if ( dielectricVisible ) {
            addChild( dielectricOffsetDragHandleNode );
        }
        addChild( plateSeparationDragHandleNode );
        addChild( plateAreaDragHandleNode );
        addChild( batteryConnectionButtonNode );
        addChild( plateChargeControlNode );

        // layout
        {
            double x, y;

            // wires shapes are in model coordinate frame, so the nodes live at (0,0)
            topWireNode.setOffset( 0, 0 );
            bottomWireNode.setOffset( 0, 0 );

            // battery
            batteryNode.setOffset( mvt.modelToView( circuit.getBattery().getLocationReference() ) );

            // capacitor
            capacitorNode.setOffset( mvt.modelToView( circuit.getCapacitor().getLocation() ) );

            // top current indicator
            double topWireThickness = mvt.modelToViewDelta( circuit.getTopWire().getThickness(), 0, 0 ).getX();
            x = topWireNode.getFullBoundsReference().getCenterX();
            y = topWireNode.getFullBoundsReference().getMinY() + ( topWireThickness / 2 );
            topCurrentIndicatorNode.setOffset( x, y );

            // bottom current indicator
            double bottomWireThickness = mvt.modelToViewDelta( circuit.getBottomWire().getThickness(), 0, 0 ).getX();
            x = bottomWireNode.getFullBoundsReference().getCenterX();
            y = bottomWireNode.getFullBoundsReference().getMaxY() - ( bottomWireThickness / 2 );
            bottomCurrentIndicatorNode.setOffset( x, y );

            // Connect/Disconnect Battery button
            x = batteryNode.getFullBoundsReference().getMinX();
            y = topCurrentIndicatorNode.getFullBoundsReference().getMinY() - batteryConnectionButtonNode.getFullBoundsReference().getHeight() - 10;
            batteryConnectionButtonNode.setOffset( x, y );

            // Plate Charge control
            plateChargeControlNode.setOffset( mvt.modelToView( new Point3D.Double( circuit.getCapacitor().getX() - 0.004, 0.001, 0 ) ) );
        }

        // observers
        circuit.addCircuitChangeListener( new CircuitChangeListener() {
            public void circuitChanged() {
                updateBatteryConnectivity();
            }
        } );

        // default state
        updateBatteryConnectivity();
    }

    // This method must be called if the model element has a longer lifetime than the view.
    public void cleanup() {
        //FUTURE circuit.removeCircuitChangeListener
    }

    /**
     * Determines whether the dielectric is transparent or opaque.
     * This depends on which meters are selected in the Dielectric canvas, not on the dielectric properties.
     *
     * @param transparent
     */
    public void setDielectricTransparent( boolean transparent ) {
        capacitorNode.getDielectricNode().setTransparent( transparent );
    }

    // Updates the circuit components and controls to match the state of the battery connection.
    private void updateBatteryConnectivity() {
        boolean isConnected = circuit.isBatteryConnected();
        // visible when battery is connected
        topWireNode.setVisible( isConnected );
        bottomWireNode.setVisible( isConnected );
        topCurrentIndicatorNode.setVisible( isConnected );
        bottomCurrentIndicatorNode.setVisible( isConnected );
        // plate charge control
        plateChargeControlNode.setVisible( !circuit.isBatteryConnected() );
    }
}
