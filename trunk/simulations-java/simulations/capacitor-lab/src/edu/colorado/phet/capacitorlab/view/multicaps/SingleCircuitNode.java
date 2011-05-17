// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.control.CapacitanceControlNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.SingleCircuit;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
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

        // capacitor node, at its model location
        Capacitor capacitor = circuit.getCapacitor();
        CapacitorNode capacitorNode = new CapacitorNode( capacitor, mvt, plateChargeVisible, eFieldVisible, dielectricChargeView,
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
