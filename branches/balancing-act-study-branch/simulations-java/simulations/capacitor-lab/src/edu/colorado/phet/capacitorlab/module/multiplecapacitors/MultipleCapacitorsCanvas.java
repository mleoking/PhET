// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.developer.EFieldShapesDebugNode;
import edu.colorado.phet.capacitorlab.developer.VoltageShapesDebugNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.capacitorlab.view.MultipleCapacitorsCircuitNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.EFieldDetectorNode;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterNode;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Canvas for the "Multiple Capacitors" module.
 * </p>
 * This canvas has much in common with DielectricCanvas, but was developed added much later, uses a different
 * representation for circuits, has different parametrizations of some view components.  I attempted to move some
 * of the common bits into the base class, but it because messy and less readable. So I decided that a bit of
 * duplication is preferable here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsCanvas extends CLCanvas {

    private final MultipleCapacitorsModel model;
    private final PNode circuitParentNode; // parent of all circuit nodes, so we don't have to mess with rendering order
    private final BarMeterNode capacitanceMeterNode, plateChargeMeterNode, storedEnergyMeterNode;
    private final PNode shapesDebugParentNode; // debugging shapes, developer control

    public MultipleCapacitorsCanvas( final MultipleCapacitorsModel model, final CLModelViewTransform3D mvt, CLGlobalProperties globalProperties ) {
        super( model, mvt, globalProperties );

        this.model = model;

        /*
         * Maximums, for calibrating various view representations.
         * Note that for charge and fields, we get these values from the DielectricModel,
         * so that density of charge and field will be the same across all modules.
         */
        final double maxPlateCharge = DielectricModel.getMaxPlateCharge();
        final double maxExcessDielectricPlateCharge = DielectricModel.getMaxExcessDielectricPlateCharge();
        final double maxEffectiveEField = DielectricModel.getMaxEffectiveEField();
        final double maxDielectricEField = DielectricModel.getMaxDielectricEField();
        final double eFieldReferenceMagnitude = MultipleCapacitorsModel.getEFieldReferenceMagnitude();

        // One node for each circuit. These persist so we don't have to do cleanup when the current circuit changes.
        circuitParentNode = new PNode();
        for ( ICircuit circuit : model.getCircuits() ) {
            PNode node = new MultipleCapacitorsCircuitNode( circuit, mvt, false /* dielectricVisible */,
                                                            getPlateChargesVisibleProperty(), getEFieldVisibleProperty(), getDielectricChargeViewProperty(),
                                                            maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
            node.setVisible( false );
            circuitParentNode.addChild( node );
        }

        // meters
        capacitanceMeterNode = new CapacitanceMeterNode( model.capacitanceMeter, mvt, CLStrings.TOTAL_CAPACITANCE );
        plateChargeMeterNode = new PlateChargeMeterNode( model.plateChargeMeter, mvt, CLStrings.STORED_CHARGE );
        storedEnergyMeterNode = new StoredEnergyMeterNode( model.storedEnergyMeter, mvt, CLStrings.STORED_ENERGY );
        VoltmeterNode voltmeterNode = new VoltmeterNode( model.voltmeter, mvt );
        EFieldDetectorNode eFieldDetectorNode = new EFieldDetectorNode( model.eFieldDetector, mvt, eFieldReferenceMagnitude, globalProperties.dev, true /* eFieldDetectorSimplified */ );

        // debug
        shapesDebugParentNode = new PComposite();

        // rendering order
        addChild( circuitParentNode );
        addChild( capacitanceMeterNode );
        addChild( plateChargeMeterNode );
        addChild( storedEnergyMeterNode );
        addChild( eFieldDetectorNode );
        addChild( voltmeterNode );
        addChild( shapesDebugParentNode );

        // When the current circuit changes, make the proper circuit node visible.
        model.currentCircuitProperty.addObserver( new SimpleObserver() {
            public void update() {
                updateCircuitNodes();
            }
        } );

        // change visibility of debug shapes
        RichSimpleObserver shapesVisibilityObserver = new RichSimpleObserver() {
            public void update() {
                updateShapesDebugNodes();
            }
        };
        shapesVisibilityObserver.observe( globalProperties.eFieldShapesVisibleProperty, globalProperties.voltageShapesVisibleProperty );
    }

    public void reset() {
        super.reset();
        // zoom level of bar meters
        capacitanceMeterNode.reset();
        plateChargeMeterNode.reset();
        storedEnergyMeterNode.reset();
    }

    // Updates visibility of circuit nodes, so that the node corresponding to the current circuit is visible.
    private void updateCircuitNodes() {
        int numberOfChildren = circuitParentNode.getChildrenCount();
        for ( int i = 0; i < numberOfChildren; i++ ) {
            PNode child = circuitParentNode.getChild( i );
            if ( child instanceof MultipleCapacitorsCircuitNode ) {
                MultipleCapacitorsCircuitNode circuitNode = (MultipleCapacitorsCircuitNode) child;
                circuitNode.setVisible( model.currentCircuitProperty.get() == circuitNode.getCircuit() );
            }
        }
        updateShapesDebugNodes();
    }

    // Updates the debugging shapes by recreating them. Quick and dirty, because this is a developer feature.
    private void updateShapesDebugNodes() {
        shapesDebugParentNode.removeAllChildren();

        PNode voltageShapesDebugNode = new VoltageShapesDebugNode( model.currentCircuitProperty.get(), model.voltmeter );
        shapesDebugParentNode.addChild( voltageShapesDebugNode );
        voltageShapesDebugNode.setVisible( getGlobalProperties().voltageShapesVisibleProperty.get() );

        PNode eFieldShapesDebugNode = new EFieldShapesDebugNode( model.currentCircuitProperty.get() );
        shapesDebugParentNode.addChild( eFieldShapesDebugNode );
        eFieldShapesDebugNode.setVisible( getGlobalProperties().eFieldShapesVisibleProperty.get() );
    }
}
