// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.SingleCircuit;
import edu.colorado.phet.capacitorlab.view.WireNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;

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
        super( circuit, mvt, plateChargeVisible, eFieldVisible, dielectricChargeView, maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );

        // top and bottom wires
        WireNode topWireNode = new WireNode( circuit.getTopWire() );
        addChild( topWireNode );
        WireNode bottomWireNode = new WireNode( circuit.getBottomWire() );
        addChild( bottomWireNode );

        //TODO this code is similar in all circuit nodes
        // current indicators layout
        {
            double x, y;

            // top current indicator centered on top wire
            double topWireThickness = mvt.modelToViewDelta( circuit.getTopWire().getThickness(), 0, 0 ).getX();
            x = topWireNode.getFullBoundsReference().getCenterX();
            y = topWireNode.getFullBoundsReference().getMinY() + ( topWireThickness / 2 );
            getTopCurrentIndicatorNode().setOffset( x, y );

            // bottom current indicator centered on bottom wire
            double bottomWireThickness = mvt.modelToViewDelta( circuit.getBottomWire().getThickness(), 0, 0 ).getX();
            x = bottomWireNode.getFullBoundsReference().getCenterX();
            y = bottomWireNode.getFullBoundsReference().getMaxY() - ( bottomWireThickness / 2 );
            getBottomCurrentIndicatorNode().setOffset( x, y );
        }
    }
}
