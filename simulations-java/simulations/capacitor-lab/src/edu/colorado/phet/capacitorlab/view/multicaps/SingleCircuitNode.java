// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import edu.colorado.phet.capacitorlab.control.CapacitanceControlNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.SingleCircuit;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.WireNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;

//TODO SeriesCircuitNode is a more general form of this node

/**
 * Node for a single-capacitor circuit.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SingleCircuitNode extends AbstractCircuitNode {

    public SingleCircuitNode( SingleCircuit circuit, CLModelViewTransform3D mvt,
                              Property<Boolean> plateChargeVisible, final Property<Boolean> eFieldVisible, Property<DielectricChargeView> dielectricChargeView,
                              double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {
        super( circuit, mvt );

        // capacitor
        Capacitor capacitor = circuit.getCapacitor();
        CapacitorNode capacitorNode = new CapacitorNode( capacitor, mvt, plateChargeVisible, eFieldVisible, dielectricChargeView,
                                                         maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
        capacitorNode.getDielectricNode().setVisible( false );

        // capacitance control
        CapacitanceControlNode capacitanceControlNode = new CapacitanceControlNode( capacitor,
                                                                                    MultipleCapacitorsModel.CAPACITANCE_RANGE, MultipleCapacitorsModel.CAPACITANCE_DISPLAY_EXPONENT );

        // top and bottom wires
        WireNode topWireNode = new WireNode( circuit.getTopWire(), mvt );
        WireNode bottomWireNode = new WireNode( circuit.getBottomWire(), mvt );

        // rendering order
        addChild( bottomWireNode );
        addChild( capacitorNode );
        addChild( capacitanceControlNode );
        addChild( topWireNode );

        // layout
        {
            // capacitor node at its model location
            capacitorNode.setOffset( mvt.modelToView( capacitor.getLocation() ) );

            // capacitance control to left of capacitor
            double x = capacitorNode.getFullBoundsReference().getMinX() - capacitanceControlNode.getFullBoundsReference().getWidth() - PNodeLayoutUtils.getOriginXOffset( capacitanceControlNode ) - 5;
            double y = capacitorNode.getYOffset() - ( capacitanceControlNode.getFullBoundsReference().getHeight() / 2 );
            capacitanceControlNode.setOffset( x, y );

            // wires shapes are in model coordinate frame, so the nodes live at (0,0)
            topWireNode.setOffset( 0, 0 );
            bottomWireNode.setOffset( 0, 0 );
        }
    }
}
