/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.CLModel;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
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
    private static final double WIRE_THICKNESS = 3; // mm
    private static final double WIRE_HEIGHT = 75; // mm
    
    private final CLModel model;
    private final ModelViewTransform mvt;
    private final double wireHeight, wireThickness;
    
    private final GeneralPath wirePath;
    private final PPath wireNode;

    public WireNode( CLModel model, ModelViewTransform mvt, double wireHeight, double wireThickness ) {
        
        this.model = model;
        model.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {

            @Override
            public void plateSeparationChanged() {
                update();
            }

            @Override
            public void plateSizeChanged() {
                update();
            }
            
        });
        model.getBattery().addBatteryChangeListener( new BatteryChangeAdapter() {
            @Override
            public void polarityChanged() {
                update();
            }
        });
        
        this.mvt = mvt;
        this.wireHeight = wireHeight;
        this.wireThickness = wireThickness;
        
        wirePath = new GeneralPath();
        wireNode = new PPath( wirePath );
        wireNode.setStroke( WIRE_STROKE );
        wireNode.setStrokePaint( WIRE_STROKE_COLOR );
        wireNode.setPaint( WIRE_FILL_COLOR );
        addChild( wireNode );
        
        update();
    }
    
    protected abstract void update();
    
    protected CLModel getModel() {
        return model;
    }
    
    protected ModelViewTransform getModelViewTransform() {
        return mvt;
    }
    
    protected double getWireHeight() {
        return wireHeight;
    }
    
    protected double getWireThickness() {
        return wireThickness;
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
        public TopWireNode( CLModel model, ModelViewTransform mvt ) {
            super( model, mvt, -Math.abs( WIRE_HEIGHT ), WIRE_THICKNESS );
        }
        
        protected void update() {
            
            CLModel model = getModel();
            ModelViewTransform mvt = getModelViewTransform();
            GeneralPath wirePath = getWirePath();
            PPath wireNode = getWireNode();
            
            // model-to-view transforms
            Point2D pBattery = mvt.modelToView( model.getBattery().getLocationReference() );
            Point2D pCapacitor = mvt.modelToView( model.getCapacitor().getLocationReference() );
            double wireHeight = mvt.modelToView( getWireHeight() );
            double wireThickness = mvt.modelToView( getWireThickness() );
            double plateSeparation = mvt.modelToView( model.getCapacitor().getPlateSeparation() );
            double plateThickness = mvt.modelToView( model.getCapacitor().getPlateThickness() );
            
            // HACK: alignment with plus and minus terminals of battery, values chosen by visual inspection
            double yOffsetToBatteryTerminal = ( model.getBattery().getVoltage() >= 0 ) ? 102 : 92;
            
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
        
        public BottomWireNode( CLModel model, ModelViewTransform mvt ) {
            super( model, mvt, Math.abs( WIRE_HEIGHT ), WIRE_THICKNESS );
        }
        
        protected void update() {
            
            CLModel model = getModel();
            ModelViewTransform mvt = getModelViewTransform();
            GeneralPath wirePath = getWirePath();
            PPath wireNode = getWireNode();
            
            // model-to-view transforms
            Point2D pBattery = mvt.modelToView( model.getBattery().getLocationReference() );
            Point2D pCapacitor = mvt.modelToView( model.getCapacitor().getLocationReference() );
            double wireHeight = mvt.modelToView( getWireHeight() );
            double wireThickness = mvt.modelToView( getWireThickness() );
            double plateSeparation = mvt.modelToView( model.getCapacitor().getPlateSeparation() );
            double plateThickness = mvt.modelToView( model.getCapacitor().getPlateThickness() );
            
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
