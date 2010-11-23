/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.control.AddWiresButtonNode;
import edu.colorado.phet.capacitorlab.control.PlateChargeControlNode;
import edu.colorado.phet.capacitorlab.control.RemoveWiresButtonNode;
import edu.colorado.phet.capacitorlab.developer.EFieldShapesDebugNode;
import edu.colorado.phet.capacitorlab.developer.VoltageShapesDebugNode;
import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.PlateAreaDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.PlateSeparationDragHandleNode;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.CurrentIndicatorNode;
import edu.colorado.phet.capacitorlab.view.WireNode;
import edu.colorado.phet.capacitorlab.view.meters.*;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Canvas for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {
    
    private final DielectricModel model;
    private final CLModelViewTransform3D mvt;
    
    // circuit
    private final CapacitorNode capacitorNode;
    private final BatteryNode batteryNode;
    private final AddWiresButtonNode addWiresButtonNode;
    private final RemoveWiresButtonNode removeWiresButtonNode;
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
    
    // bounds of the play area, for constraining dragging to within the play area
    private final PPath playAreaBoundsNode;
    private WireNode topWireNode;
    private WireNode bottomWireNode;

    public DielectricCanvas( final DielectricModel model, CLModelViewTransform3D mvt, boolean dev ) {
        
        this.model = model;
        model.getCircuit().addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeListener() {
            public void circuitChanged() {
                updateBatteryConnectivity();
            }
        } );
        
        this.mvt = mvt;
        
        batteryNode = new BatteryNode( model.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        capacitorNode = new CapacitorNode( model.getCircuit(), mvt, CLConstants.PLATE_CHARGES_VISIBLE, CLConstants.EFIELD_VISIBLE, CLConstants.DIELECTRIC_CHARGE_VIEW );
        topWireNode = new WireNode( model.getTopWire(), mvt );
        bottomWireNode = new WireNode( model.getBottomWire(), mvt );

        addWiresButtonNode = new AddWiresButtonNode( model.getCircuit() );
        removeWiresButtonNode = new RemoveWiresButtonNode( model.getCircuit() );
        
        dielectricOffsetDragHandleNode = new DielectricOffsetDragHandleNode( model.getCapacitor(), mvt, CLConstants.DIELECTRIC_OFFSET_RANGE );
        plateSeparationDragHandleNode = new PlateSeparationDragHandleNode( model.getCapacitor(), mvt, CLConstants.PLATE_SEPARATION_RANGE );
        plateAreaDragHandleNode = new PlateAreaDragHandleNode( model.getCapacitor(), mvt, CLConstants.PLATE_WIDTH_RANGE );
        
        playAreaBoundsNode = new PPath();
        playAreaBoundsNode.setStroke( null );
        addChild( playAreaBoundsNode );
        
        capacitanceMeterNode = new CapacitanceMeterNode( model.getCapacitanceMeter(), mvt );
        plateChargeMeterNode = new PlateChargeMeterNode( model.getPlateChargeMeter(), mvt );
        storedEnergyMeterNode = new StoredEnergyMeterNode( model.getStoredEnergyMeter(), mvt );
        voltmeter = new VoltmeterView( model.getVoltmeter(), mvt );
        eFieldDetector = new EFieldDetectorView( model.getEFieldDetector(), mvt, dev );
        
        plateChargeControNode = new PlateChargeControlNode( model.getCircuit() );
        
        topCurrentIndicatorNode = new CurrentIndicatorNode( model.getCircuit(), 0 );
        bottomCurrentIndicatorNode = new CurrentIndicatorNode( model.getCircuit(), Math.PI );
        
        voltageShapesDebugNode = new VoltageShapesDebugNode( model, mvt );
        voltageShapesDebugNode.setVisible( false );
        
        eFieldShapesDebugNode = new EFieldShapesDebugNode( model, mvt );
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
        addChild( addWiresButtonNode );
        addChild( removeWiresButtonNode );
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
            pView = mvt.modelToView( model.getCapacitor().getLocationReference() );
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
            
            // "Add Wires" button
            x = topWireNode.getFullBoundsReference().getCenterX() - ( addWiresButtonNode.getFullBoundsReference().getWidth() / 2 );
            y = topCurrentIndicatorNode.getFullBoundsReference().getMinY() - addWiresButtonNode.getFullBoundsReference().getHeight() - 10;
            addWiresButtonNode.setOffset( x, y );
            
            // "Remove Wires" button
            x = topWireNode.getFullBoundsReference().getCenterX() - ( removeWiresButtonNode.getFullBoundsReference().getWidth() / 2 );
            y = addWiresButtonNode.getYOffset();
            removeWiresButtonNode.setOffset( x, y );
            
            // Plate Charge control
            pView = mvt.modelToView( CLConstants.PLATE_CHARGE_CONTROL_LOCATION );
            plateChargeControNode.setOffset( pView  );
        }
        
        // observers 
        {
            // things whose visibility causes the dielectric to become transparent
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    boolean transparent = capacitorNode.isEFieldVisible() || model.getVoltmeter().isVisible() || model.getEFieldDetector().isVisible();
                    capacitorNode.getDielectricNode().setOpaque( !transparent );
                }
            };
            capacitorNode.addEFieldVisibleObserver( o );
            model.getVoltmeter().addVisibleObserver( o );
            model.getEFieldDetector().addVisibleObserver( o );
        }
        
        // default state
        reset();
    }
    
    public void reset() {
        // battery connectivity
        updateBatteryConnectivity();
        // capacitor view
        capacitorNode.reset();
    }
    
    public void setEFieldShapesVisible( boolean enabled ) {
        eFieldShapesDebugNode.setVisible( enabled );
    }
    
    public void setVoltageShapesVisible( boolean enabled ) {
        voltageShapesDebugNode.setVisible( enabled );
    }
    
    public CapacitorNode getCapacitorNode() {
        return capacitorNode;
    }
    
    public PNode getChargeMeterNode() {
        return plateChargeMeterNode;
    }
    
    public PNode getCapacitanceMeterNode() {
        return capacitanceMeterNode;
    }
    
    public PNode getEnergyMeterNode() {
        return storedEnergyMeterNode;
    }
    
    public VoltmeterView getVoltMeter() {
        return voltmeter;
    }
    
    public EFieldDetectorView getEFieldDetector() {
        return eFieldDetector;
    }
    
    public void setEFieldDetectorShowVectorsPanelVisible( boolean visible ) {
        eFieldDetector.setShowVectorsPanelVisible( visible );
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
        addWiresButtonNode.setVisible( !isConnected );
        removeWiresButtonNode.setVisible( isConnected );
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
        model.getWorld().setBounds( 0, 0, p.getX(), p.getY() );
        
        // Adjust play area bounds so that things aren't dragged off the canvas.
        final double margin = 0;
        playAreaBoundsNode.setPathTo( new Rectangle2D.Double( margin, margin, worldSize.getWidth() - ( 2 * margin ), worldSize.getHeight() - ( 2 * margin ) ) );
        
        // If anything draggable is outside the canvas and doesn't have an associated model element, move it inside.
        keepInsideCanvas( capacitanceMeterNode );
        keepInsideCanvas( plateChargeMeterNode );
        keepInsideCanvas( storedEnergyMeterNode );
        keepInsideCanvas( eFieldDetector.getBodyNode() );
    }
}
