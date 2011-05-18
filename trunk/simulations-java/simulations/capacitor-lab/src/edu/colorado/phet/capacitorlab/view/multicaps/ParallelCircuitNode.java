// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.control.CapacitanceControlNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.ParallelCircuit;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;

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

        //TODO add wires
    }
}
