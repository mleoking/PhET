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
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.BullseyeNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.CurrentIndicatorNode;
import edu.colorado.phet.capacitorlab.view.WireNode.BottomWireNode;
import edu.colorado.phet.capacitorlab.view.WireNode.TopWireNode;
import edu.colorado.phet.capacitorlab.view.meters.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterNode;
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
    private final BullseyeNode originNode;
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
    
    // controls
    private final PlateChargeControlNode plateChargeControNode;
    
    // drag bound
    private final Rectangle2D dragBoundsRectangle;
    private final PPath dragBoundsNode;
    
    public DielectricCanvas( final CLModel model, boolean dev ) {
        
        this.model = model;
        model.getCircuit().addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void batteryConnectedChanged() {
                updateBatteryConnectivity();
            }
        } );
        
        mvt = new ModelViewTransform( CLConstants.MVT_SCALE, CLConstants.MVT_OFFSET );
        
        batteryNode = new BatteryNode( model.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        capacitorNode = new CapacitorNode( model.getCircuit(), mvt, dev );
        topWireNode = new TopWireNode( model, mvt );
        bottomWireNode = new BottomWireNode( model, mvt );
        originNode = new BullseyeNode();
        
        addWiresButtonNode = new AddWiresButtonNode( model.getCircuit() );
        removeWiresButtonNode = new RemoveWiresButtonNode( model.getCircuit() );
        
        dielectricOffsetDragHandleNode = new DielectricOffsetDragHandleNode( model.getCapacitor(), mvt, CLConstants.DIELECTRIC_OFFSET_RANGE );
        plateSeparationDragHandleNode = new PlateSeparationDragHandleNode( model.getCapacitor(), mvt, CLConstants.PLATE_SEPARATION_RANGE );
        plateAreaDragHandleNode = new PlateAreaDragHandleNode( model.getCapacitor(), capacitorNode, mvt, CLConstants.PLATE_SIZE_RANGE );
        
        dragBoundsRectangle = new Rectangle2D.Double();
        dragBoundsNode = new PPath( dragBoundsRectangle );
        dragBoundsNode.setStroke( null );
        addChild( dragBoundsNode );
        
        capacitanceMeterNode = new CapacitanceMeterNode( model.getCircuit(), dragBoundsNode );
        chargeMeterNode = new PlateChargeMeterNode( model.getCircuit(), dragBoundsNode );
        energyMeterNode = new StoredEnergyMeterNode( model.getCircuit(), dragBoundsNode );
        voltmeterNode = new VoltmeterNode();//XXX
        
        plateChargeControNode = new PlateChargeControlNode( model.getCircuit() );
        
        topCurrentIndicatorNode = new CurrentIndicatorNode( model.getCircuit() );
        bottomCurrentIndicatorNode = new CurrentIndicatorNode( model.getCircuit() );
        bottomCurrentIndicatorNode.rotate( Math.PI );
        
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
        addChild( capacitanceMeterNode );
        addChild( chargeMeterNode );
        addChild( energyMeterNode );
        addChild( voltmeterNode );
        addChild( plateChargeControNode );
        
        // static layout
        {
            Point2D pModel = new Point2D.Double();
            Point2D pView = new Point2D.Double();
            double x, y = 0;

            // capacitor
            pModel.setLocation( model.getBattery().getLocationReference().getX(), model.getBattery().getLocationReference().getY() );
            mvt.modelToView( pModel, pView );
            batteryNode.setOffset( pView.getX(), pView.getY() );

            // battery
            pModel.setLocation( model.getCapacitor().getLocationReference().getX(), model.getCapacitor().getLocationReference().getY() );
            mvt.modelToView( pModel, pView );
            capacitorNode.setOffset( pView.getX(), pView.getY() );
            
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
            capacitanceMeterNode.setOffset( 600, 25 ); //XXX
            chargeMeterNode.setOffset( 750, 25 ); //XXX
            energyMeterNode.setOffset( 900, 25 ); //XXX
            voltmeterNode.setOffset( 750, 325 ); //XXX
            
            // Charge control, above capacitor
            x = mvt.modelToView( new Point2D.Double( 0, 0 ) ).getX();
            y = 15; //XXX
            plateChargeControNode.setOffset( x, 15  );
            
            // origin marker
            pModel.setLocation( 0, 0 );
            mvt.modelToView( pModel, pView );
            originNode.setOffset( pView.getX(), pView.getY() );
        }
        
        // default state
        updateBatteryConnectivity();
        capacitanceMeterNode.setVisible( CLConstants.CAPACITANCE_METER_VISIBLE );
        chargeMeterNode.setVisible( CLConstants.CHARGE_METER_VISIBLE );
        energyMeterNode.setVisible( CLConstants.ENERGY_METER_VISIBLE );
        voltmeterNode.setVisible( CLConstants.VOLTMETER_VISIBLE );
    }
    
    public void reset() {
        //XXX
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
        
        // Adjust drag bounds so that things aren't dragged off the canvas.
        double margin = 0;
        dragBoundsRectangle.setRect( margin, margin, worldSize.getWidth() - ( 2 * margin ), worldSize.getHeight() - ( 2 * margin ) );
        dragBoundsNode.setPathTo( dragBoundsRectangle );
        
        // If anything draggable is outside the canvas, move it inside.
        keepInsideCanvas( chargeMeterNode );
    }
}
