// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.developer.EFieldShapesDebugNode;
import edu.colorado.phet.capacitorlab.developer.VoltageShapesDebugNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.DielectricCircuitNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.EFieldDetectorNode;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterNode;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Canvas for the "Dielectric" module.
 * </p>
 * This canvas has much in common with MultipleCapacitorsCanvas, but was developed added much earlier, uses a different
 * representation for circuits, and has different parametrizations of some view components.  I attempted to move some
 * of the common bits into the base class, but it because messy and less readable. So I decided that a bit of
 * duplication is preferable here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {

    private final DielectricModel model;
    private final BarMeterNode capacitanceMeterNode, plateChargeMeterNode, storedEnergyMeterNode;
    private final PNode shapesDebugParentNode;

    public DielectricCanvas( final DielectricModel model, CLModelViewTransform3D mvt, CLGlobalProperties globalProperties,
                             boolean eFieldDetectorSimplified, boolean dielectricVisible ) {
        super( model, mvt, globalProperties );

        this.model = model;

        // Maximums, for calibrating various view representations.
        final double maxPlateCharge = DielectricModel.getMaxPlateCharge();
        final double maxExcessDielectricPlateCharge = DielectricModel.getMaxExcessDielectricPlateCharge();
        final double maxEffectiveEField = DielectricModel.getMaxEffectiveEField();
        final double maxDielectricEField = DielectricModel.getMaxDielectricEField();
        final double eFieldReferenceMagnitude = DielectricModel.getEFieldReferenceMagnitude();

        // circuit
        final DielectricCircuitNode circuitNode = new DielectricCircuitNode( model.circuit, mvt, dielectricVisible,
                                                                             getPlateChargesVisibleProperty(), getEFieldVisibleProperty(), getDielectricChargeViewProperty(),
                                                                             maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );

        // meters
        capacitanceMeterNode = new CapacitanceMeterNode( model.capacitanceMeter, mvt, CLStrings.CAPACITANCE );
        plateChargeMeterNode = new PlateChargeMeterNode( model.plateChargeMeter, mvt, CLStrings.PLATE_CHARGE_TOP );
        storedEnergyMeterNode = new StoredEnergyMeterNode( model.storedEnergyMeter, mvt, CLStrings.STORED_ENERGY );
        VoltmeterNode voltmeterNode = new VoltmeterNode( model.voltmeter, mvt );
        EFieldDetectorNode eFieldDetectorNode = new EFieldDetectorNode( model.eFieldDetector, mvt, eFieldReferenceMagnitude, globalProperties.dev, eFieldDetectorSimplified );

        // debug
        shapesDebugParentNode = new PComposite();

        // rendering order
        addChild( circuitNode );
        addChild( capacitanceMeterNode );
        addChild( plateChargeMeterNode );
        addChild( storedEnergyMeterNode );
        addChild( eFieldDetectorNode );
        addChild( voltmeterNode );
        addChild( shapesDebugParentNode );

        // watch things whose visibility causes the dielectric to become transparent
        RichSimpleObserver transparencyObserver = new RichSimpleObserver() {
            public void update() {
                boolean transparent = getEFieldVisibleProperty().get() ||
                                      model.voltmeter.isVisible() ||
                                      model.eFieldDetector.visibleProperty.get() ||
                                      getDielectricChargeViewProperty().get() == DielectricChargeView.EXCESS;
                circuitNode.setDielectricTransparent( transparent );
            }
        };
        transparencyObserver.observe( getEFieldVisibleProperty(), model.voltmeter.visibleProperty, model.eFieldDetector.visibleProperty, getDielectricChargeViewProperty() );

        // change visibility of debug shapes
        RichSimpleObserver shapesVisibilityObserver = new RichSimpleObserver() {
            public void update() {
                updateShapesDebugNodes();
            }
        };
        shapesVisibilityObserver.observe( globalProperties.eFieldShapesVisibleProperty, globalProperties.voltageShapesVisibleProperty );

        // default state
        reset();
    }

    public void reset() {
        super.reset();
        // zoom levels of bar meters
        capacitanceMeterNode.reset();
        plateChargeMeterNode.reset();
        storedEnergyMeterNode.reset();
    }

    // Updates the debugging shapes by recreating them. Quick and dirty, because this is a developer feature.
    private void updateShapesDebugNodes() {
        shapesDebugParentNode.removeAllChildren();

        PNode voltageShapesDebugNode = new VoltageShapesDebugNode( model.circuit, model.voltmeter );
        shapesDebugParentNode.addChild( voltageShapesDebugNode );
        voltageShapesDebugNode.setVisible( getGlobalProperties().voltageShapesVisibleProperty.get() );

        PNode eFieldShapesDebugNode = new EFieldShapesDebugNode( model.circuit );
        shapesDebugParentNode.addChild( eFieldShapesDebugNode );
        eFieldShapesDebugNode.setVisible( getGlobalProperties().eFieldShapesVisibleProperty.get() );
    }
}
