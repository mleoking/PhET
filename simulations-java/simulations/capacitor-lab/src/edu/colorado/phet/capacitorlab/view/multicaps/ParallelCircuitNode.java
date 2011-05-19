// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.control.CapacitanceControlNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.ParallelCircuit;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.WireNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;

/**
 * Node for a circuit with capacitors in parallel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ParallelCircuitNode extends AbstractCircuitNode {

    public ParallelCircuitNode( ParallelCircuit circuit, CLModelViewTransform3D mvt,
                                Property<Boolean> plateChargeVisible, final Property<Boolean> eFieldVisible, Property<DielectricChargeView> dielectricChargeView,
                                double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {
        super( circuit, mvt );

        // capacitors
        for ( Capacitor capacitor : circuit.getCapacitors() ) {

            // capacitor node, at its model location
            CapacitorNode capacitorNode = new CapacitorNode( capacitor, mvt,
                                                             plateChargeVisible, eFieldVisible, dielectricChargeView,
                                                             maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
            capacitorNode.getDielectricNode().setVisible( false );
            addChild( capacitorNode );
            Point2D pView = mvt.modelToView( capacitor.getLocation() );
            capacitorNode.setOffset( pView );

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
        assert ( wires.size() == 2 );

        PNode topWireNode = new WireNode( wires.get( 0 ) );
        addChild( topWireNode );
        topWireNode.moveToFront();

        PNode bottomWireNode = new WireNode( wires.get( 1 ) );
        addChild( bottomWireNode );
        bottomWireNode.moveToBack();

        //TODO this code is similar in all circuit nodes
        // current indicators
        {
            double x, y;

            // top current indicator centered on top wire, between battery and first capacitor to the right of the battery
            double topWireThickness = mvt.modelToViewDelta( wires.get( 0 ).getThickness(), 0, 0 ).getX();
            double xModel = circuit.getBattery().getX() + ( ( circuit.getCapacitors().get( 0 ).getX() - circuit.getBattery().getX() ) / 2 );
            x = mvt.modelToViewDelta( xModel, 0, 0 ).getX();
            y = topWireNode.getFullBoundsReference().getMinY() + ( topWireThickness / 2 );
            getTopCurrentIndicatorNode().setOffset( x, y );
            getTopCurrentIndicatorNode().moveToFront();

            // bottom current indicator centered on bottom wire
            double bottomWireThickness = mvt.modelToViewDelta( wires.get( wires.size() - 1 ).getThickness(), 0, 0 ).getX();
            x = getTopCurrentIndicatorNode().getXOffset();
            y = bottomWireNode.getFullBoundsReference().getMaxY() - ( bottomWireThickness / 2 );
            getBottomCurrentIndicatorNode().setOffset( x, y );
            getBottomCurrentIndicatorNode().moveToFront();
        }
    }
}
