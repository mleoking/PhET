// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.ParallelCircuit;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.view.WireNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
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
        super( circuit, mvt, plateChargeVisible, eFieldVisible, dielectricChargeView, maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );

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
        // current indicators layout
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
