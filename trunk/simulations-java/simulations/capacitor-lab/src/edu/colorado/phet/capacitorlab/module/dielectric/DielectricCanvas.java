/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.control.AddWiresButtonNode;
import edu.colorado.phet.capacitorlab.control.PlateChargeControlNode;
import edu.colorado.phet.capacitorlab.control.RemoveWiresButtonNode;
import edu.colorado.phet.capacitorlab.developer.EFieldShapesDebugNode;
import edu.colorado.phet.capacitorlab.developer.VoltageShapesDebugNode;
import edu.colorado.phet.capacitorlab.drag.DielectricOffsetDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.PlateAreaDragHandleNode;
import edu.colorado.phet.capacitorlab.drag.PlateSeparationDragHandleNode;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.BatteryNode;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
import edu.colorado.phet.capacitorlab.view.CurrentIndicatorNode;
import edu.colorado.phet.capacitorlab.view.WireNode;
import edu.colorado.phet.capacitorlab.view.meters.*;
import edu.colorado.phet.common.phetcommon.math.Point3D;
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
    private final PlateChargeMeterNode chargeMeterNode;
    private final StoredEnergyMeterNode energyMeterNode;
    private final VoltmeterView voltmeter;
    private final EFieldDetectorView eFieldDetector;
    
    // debug
    private final PNode voltageShapesDebugNode, eFieldShapesDebugNode;
    
    private final ArrayList<PNode> dielectricTransparencyNodes; // if any of these nodes is visible, the capacitor should be transparent
    
    // controls
    private final PlateChargeControlNode plateChargeControNode;
    
    // bounds of the play area, for constraining dragging to within the play area
    private final PPath playAreaBoundsNode;
    private WireNode topWireNode;
    private WireNode bottomWireNode;

    public DielectricCanvas( final DielectricModel model, CLModelViewTransform3D mvt, boolean dev ) {
        
        this.model = model;
        model.getCircuit().addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void batteryConnectedChanged() {
                updateBatteryConnectivity();
            }
        } );
        
        this.mvt = mvt;
        
        batteryNode = new BatteryNode( model.getBattery(), dev, CLConstants.BATTERY_VOLTAGE_RANGE );
        capacitorNode = new CapacitorNode( model.getCircuit(), mvt, dev );
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
        
        capacitanceMeterNode = new CapacitanceMeterNode( model.getCircuit(), playAreaBoundsNode );
        chargeMeterNode = new PlateChargeMeterNode( model.getCircuit(), playAreaBoundsNode );
        energyMeterNode = new StoredEnergyMeterNode( model.getCircuit(), playAreaBoundsNode );
        voltmeter = new VoltmeterView( model.getVoltmeter(), model.getWorld(), mvt, playAreaBoundsNode, dev );
        eFieldDetector = new EFieldDetectorView( model.getEFieldDetector(), model.getWorld(), mvt, playAreaBoundsNode, dev );
        
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
        addChild( chargeMeterNode );
        addChild( energyMeterNode );
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

        // nodes whose visibility causes the capacitor to become transparent
        dielectricTransparencyNodes = new ArrayList<PNode>();
        addDielectricTransparencyNode( capacitorNode.getEFieldNode() );
        addDielectricTransparencyNode( voltmeter.getBodyNode() );
        addDielectricTransparencyNode( eFieldDetector.getBodyNode() );
        
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
        
        // default state
        reset();
    }
    
    public void reset() {
        // battery connectivity
        updateBatteryConnectivity();
        /// visibility of various nodes
        capacitanceMeterNode.setVisible( CLConstants.CAPACITANCE_METER_VISIBLE );
        chargeMeterNode.setVisible( CLConstants.CHARGE_METER_VISIBLE );
        energyMeterNode.setVisible( CLConstants.ENERGY_METER_VISIBLE );
        capacitorNode.setPlateChargeVisible( CLConstants.PLATE_CHARGES_VISIBLE );
        capacitorNode.setEFieldVisible( CLConstants.EFIELD_VISIBLE );
        // dielectric charge view
        capacitorNode.setDielectricChargeView( CLConstants.DIELECTRIC_CHARGE_VIEW );
        // meter locations
        capacitanceMeterNode.setOffset( CLConstants.CAPACITANCE_METER_LOCATION );
        chargeMeterNode.setOffset( CLConstants.CHARGE_METER_LOCATION );
        energyMeterNode.setOffset( CLConstants.ENERGY_METER_LOCATION );
        voltmeter.getBodyNode().setOffset( CLConstants.VOLTMETER_BODY_LOCATION );
        eFieldDetector.getBodyNode().setOffset( CLConstants.EFIELD_DETECTOR_BODY_LOCATION );
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
        return chargeMeterNode;
    }
    
    public PNode getCapacitanceMeterNode() {
        return capacitanceMeterNode;
    }
    
    public PNode getEnergyMeterNode() {
        return energyMeterNode;
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
        keepInsideCanvas( chargeMeterNode );
        keepInsideCanvas( energyMeterNode );
        keepInsideCanvas( voltmeter.getBodyNode() );
        keepInsideCanvas( eFieldDetector.getBodyNode() );
    }
    
    /*
     * When certain nodes are visible, the dielectric becomes transparent.
     * Call this method to add a node to the "visibility watch list".  If any one of the nodes on
     * this list is visible, the dielectric is transparent; if none of the nodes is visible, 
     * the dielectric is opaque.
     */
    private void addDielectricTransparencyNode( PNode node ) {
        dielectricTransparencyNodes.add( node );
        node.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_VISIBLE ) ) {
                    boolean transparent = false;
                    for ( PNode node : dielectricTransparencyNodes ) {
                        if ( node.getVisible() ) {
                            transparent = true;
                            break;
                        }
                    }
                    capacitorNode.setDielectricOpaque( !transparent );
                }
            }
        } );
    }
}
