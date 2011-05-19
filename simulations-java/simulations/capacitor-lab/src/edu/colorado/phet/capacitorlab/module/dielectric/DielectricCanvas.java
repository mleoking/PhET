// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.developer.EFieldShapesDebugNode;
import edu.colorado.phet.capacitorlab.developer.VoltageShapesDebugNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.DielectricCircuitNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.EFieldDetectorView;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterView;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {

    // global view properties, directly observable
    public final Property<Boolean> plateChargesVisibleProperty = new Property<Boolean>( CLConstants.PLATE_CHARGES_VISIBLE );
    public final Property<Boolean> eFieldVisibleProperty = new Property<Boolean>( CLConstants.EFIELD_VISIBLE );
    public final Property<DielectricChargeView> dielectricChargeViewProperty = new Property<DielectricChargeView>( CLConstants.DIELECTRIC_CHARGE_VIEW );

    private final DielectricModel model;
    private final CLModelViewTransform3D mvt;

    // circuit
    private final DielectricCircuitNode circuitNode;

    // meters
    private final CapacitanceMeterNode capacitanceMeterNode;
    private final PlateChargeMeterNode plateChargeMeterNode;
    private final StoredEnergyMeterNode storedEnergyMeterNode;
    private final VoltmeterView voltmeter;
    private final EFieldDetectorView eFieldDetector;

    // debug
    private final PNode voltageShapesDebugNode, eFieldShapesDebugNode;

    public DielectricCanvas( final DielectricModel model, final CLModelViewTransform3D mvt, final CLGlobalProperties globalProperties,
                             boolean eFieldDetectorSimplified, final boolean dielectricVisible ) {

        this.model = model;
        this.mvt = mvt;

        // maximums
        final double maxPlateCharge = DielectricModel.getMaxPlateCharge();
        final double maxExcessDielectricPlateCharge = DielectricModel.getMaxExcessDielectricPlateCharge();
        final double maxEffectiveEField = DielectricModel.getMaxEffectiveEField();
        final double maxDielectricEField = DielectricModel.getMaxDielectricEField();
        final double eFieldVectorReferenceMagnitude = DielectricModel.getMaxPlatesDielectricEFieldWithBattery();

        // circuit
        circuitNode = new DielectricCircuitNode( model.getCircuit(), mvt, dielectricVisible,
                                                 plateChargesVisibleProperty, eFieldVisibleProperty, dielectricChargeViewProperty,
                                                 maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );

        // meters
        capacitanceMeterNode = new CapacitanceMeterNode( model.getCapacitanceMeter(), mvt );
        plateChargeMeterNode = new PlateChargeMeterNode( model.getPlateChargeMeter(), mvt );
        storedEnergyMeterNode = new StoredEnergyMeterNode( model.getStoredEnergyMeter(), mvt );
        voltmeter = new VoltmeterView( model.getVoltmeter(), mvt );
        eFieldDetector = new EFieldDetectorView( model.getEFieldDetector(), mvt, eFieldVectorReferenceMagnitude, globalProperties.dev, eFieldDetectorSimplified );

        voltageShapesDebugNode = new VoltageShapesDebugNode( model.getCircuit(), model.getVoltmeter() );
        voltageShapesDebugNode.setVisible( false );

        eFieldShapesDebugNode = new EFieldShapesDebugNode( model.getCircuit() );
        eFieldShapesDebugNode.setVisible( false );

        // rendering order
        addChild( circuitNode );
        addChild( capacitanceMeterNode );
        addChild( plateChargeMeterNode );
        addChild( storedEnergyMeterNode );
        addChild( eFieldDetector.getBodyNode() );
        addChild( eFieldDetector.getWireNode() );
        addChild( eFieldDetector.getProbeNode() );
        addChild( voltmeter.getBodyNode() );
        addChild( voltmeter.getPositiveProbeNode() );
        addChild( voltmeter.getPositiveWireNode() );
        addChild( voltmeter.getNegativeProbeNode() );
        addChild( voltmeter.getNegativeWireNode() );
        addChild( voltageShapesDebugNode );
        addChild( eFieldShapesDebugNode );

        // observers
        {
            // things whose visibility causes the dielectric to become transparent
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    boolean transparent = eFieldVisibleProperty.get() || model.getVoltmeter().isVisible() || model.getEFieldDetector().visibleProperty.get();
                    circuitNode.setDielectricTransparent( transparent );
                }
            };
            eFieldVisibleProperty.addObserver( o );
            model.getVoltmeter().visibleProperty.addObserver( o );
            model.getEFieldDetector().visibleProperty.addObserver( o );

            // debug shapes for measuring E-field
            globalProperties.eFieldShapesVisibleProperty.addObserver( new SimpleObserver() {
                public void update() {
                    eFieldShapesDebugNode.setVisible( globalProperties.eFieldShapesVisibleProperty.get() );
                }
            } );

            // debug shapes for measuring voltage
            globalProperties.voltageShapesVisibleProperty.addObserver( new SimpleObserver() {
                public void update() {
                    voltageShapesDebugNode.setVisible( globalProperties.voltageShapesVisibleProperty.get() );
                }
            } );
        }

        // default state
        reset();
    }

    public void reset() {
        // global properties of the view
        plateChargesVisibleProperty.reset();
        eFieldVisibleProperty.reset();
        dielectricChargeViewProperty.reset();
        // zoom levels of bar meters
        capacitanceMeterNode.reset();
        plateChargeMeterNode.reset();
        storedEnergyMeterNode.reset();
    }

    @Override
    protected void updateLayout() {
        super.updateLayout();

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }

        // adjust the model bounds
        Point3D p = mvt.viewToModelDelta( worldSize.getWidth(), worldSize.getHeight() );
        model.getWorldBounds().setBounds( 0, 0, p.getX(), p.getY() );
    }
}
