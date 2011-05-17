// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.multicaps;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.control.CapacitanceControlNode;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.module.multiplecapacitors.MultipleCapacitorsModel;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Node for a single-capacitor circuit.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SingleCircuitNode extends PhetPNode {

    public SingleCircuitNode( SingleCircuit circuit, CLModelViewTransform3D mvt,
                              Property<Boolean> plateChargeVisible, final Property<Boolean> eFieldVisible, Property<DielectricChargeView> dielectricChargeView,
                              double maxPlateCharge, double maxExcessDielectricPlateCharge, double maxEffectiveEField, double maxDielectricEField ) {

        Battery battery = circuit.getBattery();
        if ( battery != null ) {
            PNode batteryNode = new BatteryNode( circuit.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
            addChild( batteryNode );
            // battery at model location
            Point2D pView = mvt.modelToView( circuit.getBattery().getLocationReference() );
            batteryNode.setOffset( pView );
        }

        Capacitor capacitor = circuit.getCapacitor();
        if ( capacitor != null ) {

            CapacitorNode capacitorNode = new CapacitorNode( capacitor, mvt, plateChargeVisible, eFieldVisible, dielectricChargeView,
                                                             maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
            capacitorNode.getDielectricNode().setVisible( false );
            addChild( capacitorNode );
            // capacitor at model location
            Point2D pView = mvt.modelToView( capacitor.getLocation() );
            capacitorNode.setOffset( pView );

            CapacitanceControlNode capacitanceControlNode = new CapacitanceControlNode( capacitor,
                                                                                        MultipleCapacitorsModel.CAPACITANCE_RANGE, MultipleCapacitorsModel.CAPACITANCE_DISPLAY_EXPONENT );
            addChild( capacitanceControlNode );
            // control to left of capacitor
            capacitanceControlNode.setOffset( capacitorNode.getFullBoundsReference().getX() - capacitanceControlNode.getFullBoundsReference().getWidth() - 10,
                                              capacitorNode.getYOffset() - ( capacitanceControlNode.getFullBoundsReference().getHeight() / 2 ) );
        }
    }
}
