// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.control.CapacitanceControlNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.SeriesCircuit;
import edu.colorado.phet.capacitorlab.model.wire.Wire;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.WireNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;

/**
 * Node for a circuit with capacitors in series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SeriesCircuitNode extends AbstractCircuitNode {

    public SeriesCircuitNode( SeriesCircuit circuit, CLModelViewTransform3D mvt,
                              Property<Boolean> plateChargeVisible, final Property<Boolean> eFieldVisible, Property<DielectricChargeView> dielectricChargeView,
                              double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {
        super( circuit, mvt );

        ArrayList<Wire> wires = circuit.getWires();
        ArrayList<Capacitor> capacitors = circuit.getCapacitors();
        assert ( wires.size() == capacitors.size() + 1 );

        // add wires counterclockwise from the bottom terminal of the battery, so that rendering order is correct
        PNode topWireNode = null;
        PNode bottomWireNode = null;
        for ( int i = wires.size() - 1; i >= 0; i-- ) {

            WireNode wireNode = new WireNode( wires.get( i ), mvt );
            addChild( wireNode );

            if ( i == wires.size() - 1 ) {
                bottomWireNode = wireNode;
            }
            else if ( i == 0 ) {
                topWireNode = wireNode;
            }

            // move battery in front of first wire
            if ( i == wires.size() - 1 ) {
                getBatteryNode().moveToFront();
            }

            if ( i > 0 ) {
                Capacitor capacitor = capacitors.get( i - 1 );
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
        }

        //TODO this code is similar in all circuit nodes
        // Center the current indicators on the wires that are connected to the battery.
        {
            double x, y;

            // top current indicator centered on top wire
            double topWireThickness = mvt.modelToViewDelta( wires.get( 0 ).getThickness(), 0, 0 ).getX();
            x = topWireNode.getFullBoundsReference().getCenterX();
            y = topWireNode.getFullBoundsReference().getMinY() + ( topWireThickness / 2 );
            getTopCurrentIndicatorNode().setOffset( x, y );
            getTopCurrentIndicatorNode().moveToFront();

            // bottom current indicator centered on bottom wire
            double bottomWireThickness = mvt.modelToViewDelta( wires.get( wires.size() - 1 ).getThickness(), 0, 0 ).getX();
            x = bottomWireNode.getFullBoundsReference().getCenterX();
            y = bottomWireNode.getFullBoundsReference().getMaxY() - ( bottomWireThickness / 2 );
            getBottomCurrentIndicatorNode().setOffset( x, y );
            getBottomCurrentIndicatorNode().moveToFront();
        }
    }
}
