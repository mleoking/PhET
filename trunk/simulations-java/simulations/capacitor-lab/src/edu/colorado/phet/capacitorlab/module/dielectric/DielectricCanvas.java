/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.control.*;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.*;
import edu.colorado.phet.capacitorlab.view.WireNode.BottomWireNode;
import edu.colorado.phet.capacitorlab.view.WireNode.TopWireNode;
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
    
    // drag handles
    private final DielectricOffsetDragHandleNode dielectricOffsetDragHandleNode;
    private final PlateSeparationDragHandleNode plateSeparationDragHandleNode;
    private final PlateAreaDragHandleNode plateAreaDragHandleNode;
    private final DeveloperMeterNode developerMeterNode;
    
    // meters
    private final PlateChargeMeterNode plateChargeMeterNode;
    
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
        model.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {
            
            @Override
            public void dielectricOffsetChanged() {
                updateDielectricOffsetDragHandle();
            }

            @Override
            public void plateSeparationChanged() {
                updatePlateAreaDragHandle();
                updatePlateSeparationDragHandle();
            }

            @Override
            public void plateSizeChanged() {
                updatePlateAreaDragHandle();
                updatePlateSeparationDragHandle();
                updateDielectricOffsetDragHandle();
            }
        } );
        
        mvt = new ModelViewTransform( CLConstants.MVT_SCALE, CLConstants.MVT_OFFSET );
        
        batteryNode = new BatteryNode( model.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        capacitorNode = new CapacitorNode( model.getCapacitor(), mvt );
        topWireNode = new TopWireNode( model, mvt );
        bottomWireNode = new BottomWireNode( model, mvt );
        originNode = new BullseyeNode();
        
        addWiresButtonNode = new AddWiresButtonNode( model.getCircuit() );
        removeWiresButtonNode = new RemoveWiresButtonNode( model.getCircuit() );
        
        dielectricOffsetDragHandleNode = new DielectricOffsetDragHandleNode( model.getCapacitor(), mvt, CLConstants.DIELECTRIC_OFFSET_RANGE );
        plateSeparationDragHandleNode = new PlateSeparationDragHandleNode( model.getCapacitor(), mvt, CLConstants.PLATE_SEPARATION_RANGE );
        plateAreaDragHandleNode = new PlateAreaDragHandleNode( model.getCapacitor(), mvt, CLConstants.PLATE_SIZE_RANGE );
        
        dragBoundsRectangle = new Rectangle2D.Double();
        dragBoundsNode = new PPath( dragBoundsRectangle );
        dragBoundsNode.setStroke( null );
        addChild( dragBoundsNode );
        
        plateChargeMeterNode = new PlateChargeMeterNode( model.getCircuit(), dragBoundsNode );
        developerMeterNode = new DeveloperMeterNode( model, dragBoundsNode );
        
        // rendering order
        addChild( bottomWireNode );
        addChild( batteryNode );
        addChild( capacitorNode );
        addChild( topWireNode );
        addChild( dielectricOffsetDragHandleNode );
        addChild( plateSeparationDragHandleNode );
        addChild( plateAreaDragHandleNode );
        addChild( addWiresButtonNode );
        addChild( removeWiresButtonNode );
        addChild( plateChargeMeterNode );
        if ( dev ) {
            addChild( originNode );
            addChild( developerMeterNode );
        }
        
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
            
            // "Add Wires" button
            x = topWireNode.getFullBoundsReference().getCenterX() - ( addWiresButtonNode.getFullBoundsReference().getWidth() / 2 );
            y = topWireNode.getFullBoundsReference().getMinY() - addWiresButtonNode.getFullBoundsReference().getHeight() - 10;
            addWiresButtonNode.setOffset( x, y );
            
            // "Remove Wires" button
            x = topWireNode.getFullBoundsReference().getCenterX() - ( removeWiresButtonNode.getFullBoundsReference().getWidth() / 2 );
            y = topWireNode.getFullBoundsReference().getMinY() - removeWiresButtonNode.getFullBoundsReference().getHeight() - 10;
            removeWiresButtonNode.setOffset( x, y );
            
            // Charge meter
            plateChargeMeterNode.setOffset( 700, 50 ); //XXX
            
            // origin marker
            pModel.setLocation( 0, 0 );
            mvt.modelToView( pModel, pView );
            originNode.setOffset( pView.getX(), pView.getY() );
            
            // developer meter
            developerMeterNode.setOffset( 700, 50 ); //XXX
        }
        
        // default state
        updateBatteryConnectivity();
        updateDielectricOffsetDragHandle();
        updatePlateSeparationDragHandle();
        updatePlateAreaDragHandle();
        plateChargeMeterNode.setVisible( false );
        developerMeterNode.setVisible( false );
    }
    
    public void reset() {
        //XXX
    }
    
    public PlateChargeMeterNode getPlateChargeMeterNode() {
        return plateChargeMeterNode;
    }
    
    public DeveloperMeterNode getDeveloperMeterNode() {
        return developerMeterNode;
    }
    
    private void updateBatteryConnectivity() {
        boolean isConnected = model.getCircuit().isBatteryConnected();
        topWireNode.setVisible( isConnected );
        bottomWireNode.setVisible( isConnected );
        addWiresButtonNode.setVisible( !isConnected );
        removeWiresButtonNode.setVisible( isConnected );
    }
    
    private void updateDielectricOffsetDragHandle() {
        Capacitor capacitor = model.getCapacitor();
        Point2D capacitorLocation = mvt.modelToView( capacitor.getLocationReference() );
        double plateSize = mvt.modelToView( capacitor.getPlateSize() );
        double dielectricOffset = mvt.modelToView( capacitor.getDielectricOffset() );
        double x = capacitorLocation.getX() + ( plateSize / 2 ) + dielectricOffset;
        double y = capacitorLocation.getY();
        dielectricOffsetDragHandleNode.setOffset( x, y );
    }
    
    private void updatePlateSeparationDragHandle() {
        Capacitor capacitor = model.getCapacitor();
        Point2D capacitorLocation = mvt.modelToView( capacitor.getLocationReference() );
        double plateSize = mvt.modelToView( capacitor.getPlateSize() );
        double plateSeparation = mvt.modelToView( capacitor.getPlateSeparation() );
        double x = capacitorLocation.getX() - ( 0.4 * plateSize );
        double y = capacitorLocation.getY() - ( plateSeparation / 2 );
        plateSeparationDragHandleNode.setOffset( x, y );
    }
    
    private void updatePlateAreaDragHandle() {
        Capacitor capacitor = model.getCapacitor();
        Point2D capacitorLocation = mvt.modelToView( capacitor.getLocationReference() );
        Point2D dragPointOffset = capacitorNode.getPlateSizeDragPointOffsetReference();
        double x = capacitorLocation.getX() + dragPointOffset.getX();
        double y = capacitorLocation.getY() + dragPointOffset.getY();
        plateAreaDragHandleNode.setOffset( x, y );
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
        keepInsideCanvas( plateChargeMeterNode );
        keepInsideCanvas( developerMeterNode );
    }
}
