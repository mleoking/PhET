/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.control.AddWiresButtonNode;
import edu.colorado.phet.capacitorlab.control.PlateChargeControlNode;
import edu.colorado.phet.capacitorlab.control.RemoveWiresButtonNode;
import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.PlateAreaDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.PlateSeparationDragHandleNode;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.CurrentIndicatorNode;
import edu.colorado.phet.capacitorlab.view.WireNode.BottomWireNode;
import edu.colorado.phet.capacitorlab.view.WireNode.TopWireNode;
import edu.colorado.phet.capacitorlab.view.meters.*;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Canvas for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {
    
    private final CLModel model;
    private final ModelViewTransform mvt;
    
    // circuit
    private final CapacitorNode capacitorNode;
    private final BatteryNode batteryNode;
    private final TopWireNode topWireNode;
    private final BottomWireNode bottomWireNode;
    private final AddWiresButtonNode addWiresButtonNode;
    private final RemoveWiresButtonNode removeWiresButtonNode;
    private final CurrentIndicatorNode topCurrentIndicatorNode, bottomCurrentIndicatorNode;
    
    // drag handles
    private final DielectricOffsetDragHandleNode dielectricOffsetDragHandleNode;
    private final PlateSeparationDragHandleNode plateSeparationDragHandleNode;
    private final PlateAreaDragHandleNode plateAreaDragHandleNode;
    
    // meters
    private final CapacitanceMeterNode capacitanceMeterNode;
    private final PlateChargeMeterNode chargeMeterNode;
    private final StoredEnergyMeterNode energyMeterNode;
    private final VoltmeterNode voltmeterNode;
    private final EFieldDetectorNode eFieldDetectorNode;
    
    // controls
    private final PlateChargeControlNode plateChargeControNode;
    
    // bounds of the play area, for constraining dragging to within the play area
    private final PPath playAreaBoundsNode;
    
    public DielectricCanvas( final CLModel model, boolean dev ) {
        
        this.model = model;
        model.getCircuit().addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void batteryConnectedChanged() {
                updateBatteryConnectivity();
            }
        } );
        
        mvt = new ModelViewTransform( CLConstants.MVT_SCALE, CLConstants.PITCH_VIEWING_ANGLE, CLConstants.YAW_VIEWING_ANGLE );
        
        batteryNode = new BatteryNode( model.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        capacitorNode = new CapacitorNode( model.getCircuit(), mvt, dev );
        topWireNode = new TopWireNode( model, mvt );
        bottomWireNode = new BottomWireNode( model, mvt );
        
        addWiresButtonNode = new AddWiresButtonNode( model.getCircuit() );
        removeWiresButtonNode = new RemoveWiresButtonNode( model.getCircuit() );
        
        dielectricOffsetDragHandleNode = new DielectricOffsetDragHandleNode( model.getCapacitor(), mvt, CLConstants.DIELECTRIC_OFFSET_RANGE );
        plateSeparationDragHandleNode = new PlateSeparationDragHandleNode( model.getCapacitor(), mvt, CLConstants.PLATE_SEPARATION_RANGE );
        plateAreaDragHandleNode = new PlateAreaDragHandleNode( model.getCapacitor(), mvt, CLConstants.PLATE_SIZE_RANGE );
        
        playAreaBoundsNode = new PPath();
        playAreaBoundsNode.setStroke( null );
        addChild( playAreaBoundsNode );
        
        capacitanceMeterNode = new CapacitanceMeterNode( model.getCircuit(), playAreaBoundsNode );
        chargeMeterNode = new PlateChargeMeterNode( model.getCircuit(), playAreaBoundsNode );
        energyMeterNode = new StoredEnergyMeterNode( model.getCircuit(), playAreaBoundsNode );
        voltmeterNode = new VoltmeterNode();//XXX
        eFieldDetectorNode = new EFieldDetectorNode( model.getCircuit(), mvt, playAreaBoundsNode );//XXX
        
        plateChargeControNode = new PlateChargeControlNode( model.getCircuit() );
        
        topCurrentIndicatorNode = new CurrentIndicatorNode( model.getCircuit(), Polarity.POSITIVE );
        bottomCurrentIndicatorNode = new CurrentIndicatorNode( model.getCircuit(), Polarity.NEGATIVE );
        
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
        addChild( chargeMeterNode );
        addChild( energyMeterNode );
        addChild( voltmeterNode );
        addChild( eFieldDetectorNode );
        
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
            double topWireThickness = mvt.modelToView( model.getTopWire().getThickness() );
            x = topWireNode.getFullBoundsReference().getCenterX();
            y = topWireNode.getFullBoundsReference().getMinY() + ( topWireThickness / 2 );
            topCurrentIndicatorNode.setOffset( x, y );
            
            // bottom current indicator
            double bottomWireThickness = mvt.modelToView( model.getBottomWire().getThickness() );
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
            
            // Meters
            //XXX specify default locations in CLConstants, in model coordinates
            capacitanceMeterNode.setOffset( 600, 25 );
            chargeMeterNode.setOffset( 750, 25 );
            energyMeterNode.setOffset( 900, 25 );
            voltmeterNode.setOffset( 750, 325 );
            eFieldDetectorNode.setOffset( 750, 600 );
            
            // Plate Charge control
            pView = mvt.modelToView( CLConstants.PLATE_CHARGE_CONTROL_LOCATION );
            plateChargeControNode.setOffset( pView  );
        }
        
        // default state
        updateBatteryConnectivity();
        capacitanceMeterNode.setVisible( CLConstants.CAPACITANCE_METER_VISIBLE );
        chargeMeterNode.setVisible( CLConstants.CHARGE_METER_VISIBLE );
        energyMeterNode.setVisible( CLConstants.ENERGY_METER_VISIBLE );
        voltmeterNode.setVisible( CLConstants.VOLTMETER_VISIBLE );
        eFieldDetectorNode.setVisible( CLConstants.EFIELD_DETECTOR_VISIBLE );
        capacitorNode.setPlateChargeVisible( CLConstants.PLATE_CHARGES_VISIBLE );
        capacitorNode.setDielectricChargeView( CLConstants.DIELECTRIC_CHARGE_VIEW );
        capacitorNode.setEFieldVisible( CLConstants.EFIELD_VISIBLE );
        capacitorNode.setOpaque( !( CLConstants.EFIELD_VISIBLE || CLConstants.VOLTMETER_VISIBLE || CLConstants.EFIELD_DETECTOR_VISIBLE ) );
    }
    
    public void reset() {
        //XXX
    }
    
    public CapacitorNode getCapacitorNode() {
        return capacitorNode;
    }
    
    public PlateChargeMeterNode getChargeMeterNode() {
        return chargeMeterNode;
    }
    
    public CapacitanceMeterNode getCapacitanceMeterNode() {
        return capacitanceMeterNode;
    }
    
    public StoredEnergyMeterNode getEnergyMeterNode() {
        return energyMeterNode;
    }
    
    public VoltmeterNode getVoltMeterNode() {
        return voltmeterNode;
    }
    
    public EFieldDetectorNode getEFieldDetectorNode() {
        return eFieldDetectorNode;
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
        
        // Adjust play area bounds so that things aren't dragged off the canvas.
        final double margin = 0;
        playAreaBoundsNode.setPathTo( new Rectangle2D.Double( margin, margin, worldSize.getWidth() - ( 2 * margin ), worldSize.getHeight() - ( 2 * margin ) ) );
        
        // If anything draggable is outside the canvas, move it inside.
        keepInsideCanvas( capacitanceMeterNode );
        keepInsideCanvas( chargeMeterNode );
        keepInsideCanvas( energyMeterNode );
        keepInsideCanvas( voltmeterNode );  //XXX does this work? or do we need to do this for separate draggable parts of voltmeter?
    }
}
