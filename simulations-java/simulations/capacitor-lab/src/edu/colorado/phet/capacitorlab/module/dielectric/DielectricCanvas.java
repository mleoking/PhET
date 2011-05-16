// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.control.BatteryConnectionButtonNode;
import edu.colorado.phet.capacitorlab.control.PlateChargeControlNode;
import edu.colorado.phet.capacitorlab.developer.EFieldShapesDebugNode;
import edu.colorado.phet.capacitorlab.developer.VoltageShapesDebugNode;
import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.PlateAreaDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.PlateSeparationDragHandleNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.ICircuit.CircuitChangeListener;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.CurrentIndicatorNode;
import edu.colorado.phet.capacitorlab.view.WireNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.EFieldDetectorView;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterView;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {

    // global view properties, directly observable
    public final Property<Boolean> plateChargesVisible = new Property<Boolean>( CLConstants.PLATE_CHARGES_VISIBLE );
    public final Property<Boolean> eFieldVisible = new Property<Boolean>( CLConstants.EFIELD_VISIBLE );
    public final Property<DielectricChargeView> dielectricChargeView = new Property<DielectricChargeView>( CLConstants.DIELECTRIC_CHARGE_VIEW );

    private final DielectricModel model;
    private final CLModelViewTransform3D mvt;

    // circuit
    private final CapacitorNode capacitorNode;
    private final BatteryNode batteryNode;
    private final WireNode topWireNode, bottomWireNode;
    private final BatteryConnectionButtonNode batteryConnectionButtonNode;
    private final CurrentIndicatorNode topCurrentIndicatorNode, bottomCurrentIndicatorNode;

    // drag handles
    private final DielectricOffsetDragHandleNode dielectricOffsetDragHandleNode;
    private final PlateSeparationDragHandleNode plateSeparationDragHandleNode;
    private final PlateAreaDragHandleNode plateAreaDragHandleNode;

    // meters
    private final CapacitanceMeterNode capacitanceMeterNode;
    private final PlateChargeMeterNode plateChargeMeterNode;
    private final StoredEnergyMeterNode storedEnergyMeterNode;
    private final VoltmeterView voltmeter;
    private final EFieldDetectorView eFieldDetector;

    // debug
    private final PNode voltageShapesDebugNode, eFieldShapesDebugNode;

    // controls
    private final PlateChargeControlNode plateChargeControNode;

    public DielectricCanvas( final DielectricModel model, CLModelViewTransform3D mvt, final CLGlobalProperties globalProperties ) {

        this.model = model;
        this.mvt = mvt;

        // maximums
        final double maxPlateCharge = DielectricModel.getMaxPlateCharge();
        final double maxExcessDielectricPlateCharge = DielectricModel.getMaxExcessDielectricPlateCharge();
        final double maxEffectiveEfield = DielectricModel.getMaxEffectiveEfield();
        final double maxDielectricEField = DielectricModel.getMaxDielectricEField();
        final double eFieldVectorReferenceMagnitude = DielectricModel.getMaxPlatesDielectricEFieldWithBattery();

        batteryNode = new BatteryNode( model.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        capacitorNode = new CapacitorNode( model.getCircuit().getCapacitor(), mvt, plateChargesVisible, eFieldVisible, dielectricChargeView,
                                           maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEfield, maxDielectricEField );
        topWireNode = new WireNode( model.getTopWire(), mvt );
        bottomWireNode = new WireNode( model.getBottomWire(), mvt );

        batteryConnectionButtonNode = new BatteryConnectionButtonNode( model.getCircuit() );

        dielectricOffsetDragHandleNode = new DielectricOffsetDragHandleNode( model.getCapacitor(), mvt, CLConstants.DIELECTRIC_OFFSET_RANGE );
        plateSeparationDragHandleNode = new PlateSeparationDragHandleNode( model.getCapacitor(), mvt, CLConstants.PLATE_SEPARATION_RANGE );
        plateAreaDragHandleNode = new PlateAreaDragHandleNode( model.getCapacitor(), mvt, CLConstants.PLATE_WIDTH_RANGE );

        capacitanceMeterNode = new CapacitanceMeterNode( model.getCapacitanceMeter(), mvt );
        plateChargeMeterNode = new PlateChargeMeterNode( model.getPlateChargeMeter(), mvt );
        storedEnergyMeterNode = new StoredEnergyMeterNode( model.getStoredEnergyMeter(), mvt );
        voltmeter = new VoltmeterView( model.getVoltmeter(), mvt );
        eFieldDetector = new EFieldDetectorView( model.getEFieldDetector(), mvt, eFieldVectorReferenceMagnitude, globalProperties.dev );

        plateChargeControNode = new PlateChargeControlNode( model.getCircuit(), new DoubleRange( -maxPlateCharge, maxPlateCharge ) );

        topCurrentIndicatorNode = new CurrentIndicatorNode( model.getCircuit(), 0 );
        bottomCurrentIndicatorNode = new CurrentIndicatorNode( model.getCircuit(), Math.PI );

        voltageShapesDebugNode = new VoltageShapesDebugNode( model );
        voltageShapesDebugNode.setVisible( false );

        eFieldShapesDebugNode = new EFieldShapesDebugNode( model );
        eFieldShapesDebugNode.setVisible( false );

        // rendering order
        addChild( bottomWireNode );
        addChild( batteryNode );
        addChild( capacitorNode );
        addChild( topWireNode );
        addChild( topCurrentIndicatorNode );
        addChild( bottomCurrentIndicatorNode );
        addChild( dielectricOffsetDragHandleNode );
        addChild( plateSeparationDragHandleNode );
        addChild( plateAreaDragHandleNode );
        addChild( batteryConnectionButtonNode );
        addChild( plateChargeControNode );
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

        // static layout
        {
            Point2D pView = null;
            double x, y = 0;

            // battery
            pView = mvt.modelToView( model.getBattery().getLocationReference() );
            batteryNode.setOffset( pView );

            // capacitor
            pView = mvt.modelToView( model.getCapacitor().getLocation() );
            capacitorNode.setOffset( pView );

            // top current indicator
            double topWireThickness = mvt.modelToViewDelta( model.getTopWire().getThickness(), 0, 0 ).getX();
            x = topWireNode.getFullBoundsReference().getCenterX();
            y = topWireNode.getFullBoundsReference().getMinY() + ( topWireThickness / 2 );
            topCurrentIndicatorNode.setOffset( x, y );

            // bottom current indicator
            double bottomWireThickness = mvt.modelToViewDelta( model.getBottomWire().getThickness(), 0, 0 ).getX();
            x = bottomWireNode.getFullBoundsReference().getCenterX();
            y = bottomWireNode.getFullBoundsReference().getMaxY() - ( bottomWireThickness / 2 );
            bottomCurrentIndicatorNode.setOffset( x, y );

            // Connect/Disconnect Battery button
            x = batteryNode.getFullBoundsReference().getMinX();
            y = topCurrentIndicatorNode.getFullBoundsReference().getMinY() - batteryConnectionButtonNode.getFullBoundsReference().getHeight() - 10;
            batteryConnectionButtonNode.setOffset( x, y );

            // Plate Charge control
            pView = mvt.modelToView( CLConstants.PLATE_CHARGE_CONTROL_LOCATION );
            plateChargeControNode.setOffset( pView );
        }

        // observers
        {
            model.getCircuit().addCircuitChangeListener( new CircuitChangeListener() {
                public void circuitChanged() {
                    updateBatteryConnectivity();
                }
            } );

            // things whose visibility causes the dielectric to become transparent
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    boolean transparent = eFieldVisible.get() || model.getVoltmeter().isVisible() || model.getEFieldDetector().visibleProperty.get();
                    capacitorNode.getDielectricNode().setOpaque( !transparent );
                }
            };
            eFieldVisible.addObserver( o );
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
        plateChargesVisible.reset();
        eFieldVisible.reset();
        dielectricChargeView.reset();
        // battery connectivity
        updateBatteryConnectivity();
        // zoom level of bar meters
        capacitanceMeterNode.reset();
        plateChargeMeterNode.reset();
        storedEnergyMeterNode.reset();
    }

    public void setEFieldDetectorSimplified( boolean simplified ) {
        eFieldDetector.setSimplified( simplified );
    }

    public void setDielectricVisible( boolean visible ) {
        capacitorNode.setDielectricVisible( visible );
        dielectricOffsetDragHandleNode.setVisible( visible );
    }

    private void updateBatteryConnectivity() {
        boolean isConnected = model.getCircuit().isBatteryConnected();
        // visible when battery is connected
        topWireNode.setVisible( isConnected );
        bottomWireNode.setVisible( isConnected );
        topCurrentIndicatorNode.setVisible( isConnected );
        bottomCurrentIndicatorNode.setVisible( isConnected );
        // visible when battery is disconnected
        plateChargeControNode.setVisible( !isConnected );
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
