/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for wire representations.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class WireNode extends PComposite {
    
    private static final Stroke WIRE_STROKE = new BasicStroke( 1f );
    private static final Color WIRE_STROKE_COLOR = Color.BLACK;
    private static final Color WIRE_FILL_COLOR = Color.LIGHT_GRAY;
    
    private final Wire wire;
    private final Capacitor capacitor;
    private final Battery battery;
    private final ModelViewTransform mvt;
    
    private final GeneralPath wirePath;
    private final PPath wireNode;

    public WireNode( Wire wire, Capacitor capacitor, Battery battery, ModelViewTransform mvt ) {
        
        this.wire = wire;
        
        this.capacitor = capacitor;
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {

            @Override
            public void plateSeparationChanged() {
                update();
            }

            @Override
            public void plateSizeChanged() {
                update();
            }
            
        });
        
        this.battery = battery;
        battery.addBatteryChangeListener( new BatteryChangeAdapter() {
            @Override
            public void polarityChanged() {
                update();
            }
        });
        
        this.mvt = mvt;
        
        wirePath = new GeneralPath();
        wireNode = new PPath( wirePath );
        wireNode.setStroke( WIRE_STROKE );
        wireNode.setStrokePaint( WIRE_STROKE_COLOR );
        wireNode.setPaint( WIRE_FILL_COLOR );
        addChild( wireNode );
        
        update();
    }
    
    protected abstract void update();
    
    protected Wire getWire() {
        return wire;
    }
    
    protected Capacitor getCapacitor() {
        return capacitor;
    }
    
    protected Battery getBattery() {
        return battery;
    }
    
    protected ModelViewTransform getModelViewTransform() {
        return mvt;
    }
    
    protected GeneralPath getWirePath() {
        return wirePath;
    }
    
    protected PPath getWireNode() {
        return wireNode;
    }
    
    /**
     * Wire that connects the top of the battery to the top of the capacitor.
     */
    public static class TopWireNode extends WireNode {
        public TopWireNode( DielectricModel model, ModelViewTransform mvt ) {
            super( model.getTopWire(), model.getCapacitor(), model.getBattery(), mvt );
        }
        
        protected void update() {
            
            Wire wire = getWire();
            Capacitor capacitor = getCapacitor();
            Battery battery = getBattery();
            ModelViewTransform mvt = getModelViewTransform();
            GeneralPath wirePath = getWirePath();
            PPath wireNode = getWireNode();
            
            // model-to-view transforms
            Point2D pBattery = mvt.modelToView( battery.getLocationReference() );
            Point2D pCapacitor = mvt.modelToView( capacitor.getLocationReference() );
            double wireHeight = -Math.abs( mvt.modelToView( wire.getExtent() ) );
            double wireThickness = mvt.modelToView( wire.getThickness() );
            double plateSeparation = mvt.modelToView( capacitor.getPlateSeparation() );
            double plateThickness = mvt.modelToView( capacitor.getPlateThickness() );
            
            // HACK: alignment with plus and minus terminals of battery, values chosen by visual inspection
            double yOffsetToBatteryTerminal = ( battery.getVoltage() >= 0 ) ? 102 : 92;
            
            // geometry
            wirePath.reset();
            wirePath.moveTo( (float) ( pBattery.getX() - ( wireThickness / 2 ) ), (float) ( pBattery.getY() - yOffsetToBatteryTerminal ) );
            wirePath.lineTo( (float) ( pBattery.getX() - ( wireThickness / 2 ) ), (float) ( pBattery.getY() + wireHeight ) );
            wirePath.lineTo( (float) ( pCapacitor.getX() + ( wireThickness / 2 ) ), (float) ( pBattery.getY() + wireHeight ) );
            wirePath.lineTo( (float) ( pCapacitor.getX() + ( wireThickness / 2 ) ), (float) ( pCapacitor.getY() - ( plateSeparation / 2 ) - plateThickness ) );
            wirePath.lineTo( (float) ( pCapacitor.getX() - ( wireThickness / 2 ) ), (float) ( pCapacitor.getY() - ( plateSeparation / 2 ) - plateThickness ) );
            wirePath.lineTo( (float) ( pCapacitor.getX() - ( wireThickness / 2 ) ), (float) ( pBattery.getY() + wireHeight + wireThickness ) );
            wirePath.lineTo( (float) ( pBattery.getX() + ( wireThickness / 2 ) ), (float) ( pBattery.getY() + wireHeight + wireThickness ) );
            wirePath.lineTo( (float) ( pBattery.getX() + ( wireThickness / 2 ) ), (float) ( pBattery.getY() - yOffsetToBatteryTerminal ) );
            wirePath.closePath();
            wireNode.setPathTo( wirePath );
        }
    }
    
    /**
     * Wire that connects the bottom of the battery to the bottom of the capacitor.
     */
    public static class BottomWireNode extends WireNode {
        
        public BottomWireNode( DielectricModel model, ModelViewTransform mvt ) {
            super( model.getBottomWire(), model.getCapacitor(), model.getBattery(), mvt );
        }
        
        protected void update() {
            
            Wire wire = getWire();
            Capacitor capacitor = getCapacitor();
            Battery battery = getBattery();
            ModelViewTransform mvt = getModelViewTransform();
            GeneralPath wirePath = getWirePath();
            PPath wireNode = getWireNode();
            
            // model-to-view transforms
            Point2D pBattery = mvt.modelToView( battery.getLocationReference() );
            Point2D pCapacitor = mvt.modelToView( capacitor.getLocationReference() );
            double wireHeight = Math.abs( mvt.modelToView( wire.getExtent() ) );
            double wireThickness = mvt.modelToView( wire.getThickness() );
            double plateSeparation = mvt.modelToView( capacitor.getPlateSeparation() );
            double plateThickness = mvt.modelToView( capacitor.getPlateThickness() );
            
            // geometry
            wirePath.reset();
            wirePath.moveTo( (float) ( pBattery.getX() - ( wireThickness / 2 ) ), (float) ( pBattery.getY() ) );
            wirePath.lineTo( (float) ( pBattery.getX() - ( wireThickness / 2 ) ), (float) ( pBattery.getY() + wireHeight ) );
            wirePath.lineTo( (float) ( pCapacitor.getX() + ( wireThickness / 2 ) ), (float) ( pBattery.getY() + wireHeight ) );
            wirePath.lineTo( (float) ( pCapacitor.getX() + ( wireThickness / 2 ) ), (float) ( pCapacitor.getY() + ( plateSeparation / 2 ) + plateThickness ) );
            wirePath.lineTo( (float) ( pCapacitor.getX() - ( wireThickness / 2 ) ), (float) ( pCapacitor.getY() + ( plateSeparation / 2 ) + plateThickness ) );
            wirePath.lineTo( (float) ( pCapacitor.getX() - ( wireThickness / 2 ) ), (float) ( pBattery.getY() + wireHeight - wireThickness ) );
            wirePath.lineTo( (float) ( pBattery.getX() + ( wireThickness / 2 ) ), (float) ( pBattery.getY() + wireHeight - wireThickness ) );
            wirePath.lineTo( (float) ( pBattery.getX() + ( wireThickness / 2 ) ), (float) ( pBattery.getY() ) );
            wirePath.closePath();
            wireNode.setPathTo( wirePath );
        }
    }
    
    
}
