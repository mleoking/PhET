// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.Combination2Circuit;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.view.WireNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.umd.cs.piccolo.PNode;

/**
 * Node for a circuit with a parallel and series combination of capacitors.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Combination2CircuitNode extends AbstractCircuitNode {

    public Combination2CircuitNode( Combination2Circuit circuit, CLModelViewTransform3D mvt,
                                    Property<Boolean> plateChargeVisible, final Property<Boolean> eFieldVisible, Property<DielectricChargeView> dielectricChargeView,
                                    double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {
        super( circuit, mvt, plateChargeVisible, eFieldVisible, dielectricChargeView, maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );

        // wires
        ArrayList<Wire> wires = circuit.getWires();
        PNode topWireNode = null;
        PNode bottomWireNode = null;
        for ( int i = 0; i < wires.size(); i++ ) {
            WireNode wireNode = new WireNode( wires.get( i ) );
            addChild( wireNode );

            if ( i == 0 ) {
                topWireNode = wireNode;
            }
            else if ( i == wires.size() - 1 ) {
                bottomWireNode = wireNode;
            }
        }

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
