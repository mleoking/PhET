/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.control.ConnectButtonNode;
import edu.colorado.phet.capacitorlab.control.DielectricOffsetDragHandleNode;
import edu.colorado.phet.capacitorlab.control.DisconnectButtonNode;
import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.BullseyeNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.WireNode.BottomWireNode;
import edu.colorado.phet.capacitorlab.view.WireNode.TopWireNode;

/**
 * Canvas for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {
    
    private final CLModel model;
    private final ModelViewTransform mvt;
    
    private final CapacitorNode capacitorNode;
    private final BatteryNode batteryNode;
    private final BullseyeNode originNode;
    private final TopWireNode topWireNode;
    private final BottomWireNode bottomWireNode;
    private final ConnectButtonNode connectButtonNode;
    private final DisconnectButtonNode disconnectButtonNode;
    private final DielectricOffsetDragHandleNode dielectricOffsetDragHandleNode;
    
    public DielectricCanvas( final CLModel model, boolean dev ) {
        
        this.model = model;
        model.getBattery().addBatteryChangeListener( new BatteryChangeAdapter() {
            @Override
            public void connectedChanged() {
                updateConnectivity();
            }
           
        });
        model.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {
            
            @Override
            public void dielectricOffsetChanged() {
                updateDielectricOffsetDragHandle();
            }

            @Override
            public void plateSeparationChanged() {
                updateDielectricOffsetDragHandle();
                
            }

            @Override
            public void plateSizeChanged() {
                updateDielectricOffsetDragHandle();
            }
        });
        
        mvt = new ModelViewTransform( CLConstants.MVT_SCALE, CLConstants.MVT_OFFSET );
        
        batteryNode = new BatteryNode( model.getBattery(), CLConstants.BATTERY_VOLTAGE_RANGE );
        capacitorNode = new CapacitorNode( model.getCapacitor(), mvt );
        topWireNode = new TopWireNode( model, mvt );
        bottomWireNode = new BottomWireNode( model, mvt );
        originNode = new BullseyeNode();
        
        connectButtonNode = new ConnectButtonNode( model.getBattery() );
        disconnectButtonNode = new DisconnectButtonNode( model.getBattery() );
        
        dielectricOffsetDragHandleNode = new DielectricOffsetDragHandleNode( model.getCapacitor() );
        
        // rendering order
        addChild( bottomWireNode );
        addChild( batteryNode );
        addChild( capacitorNode );
        addChild( topWireNode );
        addChild( dielectricOffsetDragHandleNode );
        addChild( connectButtonNode );
        addChild( disconnectButtonNode );
        if ( dev ) {
            addChild( originNode );
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
            
            // connect button
            x = batteryNode.getFullBoundsReference().getMinX() - connectButtonNode.getFullBoundsReference().getWidth() + 35;
            y = batteryNode.getFullBoundsReference().getMinY() - connectButtonNode.getFullBoundsReference().getHeight() - 10;
            connectButtonNode.setOffset( x, y );
            
            // disconnect button
            x = batteryNode.getFullBoundsReference().getMinX() - disconnectButtonNode.getFullBoundsReference().getWidth() + 35;
            y = batteryNode.getFullBoundsReference().getMinY() - disconnectButtonNode.getFullBoundsReference().getHeight() - 10;
            disconnectButtonNode.setOffset( x, y );
            
            // origin marker
            pModel.setLocation( 0, 0 );
            mvt.modelToView( pModel, pView );
            originNode.setOffset( pView.getX(), pView.getY() );
        }
        
        // default state
        updateConnectivity();
        updateDielectricOffsetDragHandle();
    }
    
    public void reset() {
        //XXX
    }
    
    private void updateConnectivity() {
        boolean isConnected = model.getBattery().isConnected();
        topWireNode.setVisible( isConnected );
        bottomWireNode.setVisible( isConnected );
        connectButtonNode.setVisible( !isConnected );
        disconnectButtonNode.setVisible( isConnected );
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
}
