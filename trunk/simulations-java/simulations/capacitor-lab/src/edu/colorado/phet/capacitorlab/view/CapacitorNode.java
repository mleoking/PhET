/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.view.DielectricNode.DielectricChargeView;
import edu.colorado.phet.capacitorlab.view.PlateNode.BottomPlateNode;
import edu.colorado.phet.capacitorlab.view.PlateNode.TopPlateNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Visual representation of a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorNode extends PhetPNode {
    
    private final static float TRANSPARENCY = 0.75f;

    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final PlateNode topPlateNode, bottomPlateNode;
    private final DielectricNode dielectricNode;
    private final EFieldNode eFieldNode;
    private final EventListenerList listeners;
    
    public CapacitorNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
        
        this.circuit = circuit;
        this.circuit.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {

            @Override
            public void dielectricOffsetChanged() {
                updateDielectricOffset();
            }

            @Override
            public void plateSeparationChanged() {
                updateGeometry();
            }

            @Override
            public void plateSizeChanged() {
                updateGeometry();
            }
        });
        
        this.mvt = mvt;
        this.listeners = new EventListenerList();
        
        // child nodes
        topPlateNode = new TopPlateNode( circuit, mvt );
        bottomPlateNode = new BottomPlateNode( circuit, mvt );
        dielectricNode = new DielectricNode( circuit, mvt, dev, CLConstants.DIELECTRIC_OFFSET_RANGE );
        eFieldNode = new EFieldNode( circuit, mvt );
        
        // rendering order
        addChild( bottomPlateNode );
        addChild( eFieldNode );
        addChild( dielectricNode ); // dielectric between the plates
        addChild( topPlateNode );
        
        // default state
        updateGeometry();
        updateDielectricColor();
    }
    
    /**
     * Controls the opacity of the dielectric.
     * This is needed because the dielectric must be transparent to see E-field.
     * @param opaque
     */
    public void setDielectricOpaque( boolean opaque ) {
        float transparency = ( opaque ) ? 1f : TRANSPARENCY;
        /*
         * Some dielectric materials are naturally transparent.
         * Modify dielectric transparency only if its not already transparent. 
         */
        if ( circuit.getCapacitor().getDielectricMaterial().isOpaque() ) {
            dielectricNode.setTransparency( transparency );
        }
    }
    
    public void setDielectricVisible( boolean visible ) {
        dielectricNode.setVisible( visible );
    }
    
    public void setPlateChargeVisible( boolean visible ) {
        if ( visible != topPlateNode.isChargeVisible() ) {
            topPlateNode.setChargeVisible( visible );
            bottomPlateNode.setChargeVisible( visible );
            firePlateChargeVisibleChanged();
        }
    }
    
    public boolean isPlateChargeVisible() {
        return topPlateNode.isChargeVisible();
    }
    
    public void setDielectricChargeView( DielectricChargeView view ) {
        if ( !view.equals( dielectricNode.getDielectricChargeView() ) ) {
            dielectricNode.setDielectricChargeView( view );
            fireDielectricChargeViewChanged();
        }
    }
    
    public DielectricChargeView getDielectricChargeView() {
        return dielectricNode.getDielectricChargeView();
    }
    
    public void setEFieldVisible( boolean visible ) {
        if ( visible != eFieldNode.isVisible() ) {
            eFieldNode.setVisible( visible );
            fireEFieldVisibleChanged();
        }
    }
    
    public boolean isEFieldVisible() {
        return eFieldNode.isVisible();
    }
    
    /**
     * Provides so we can watch visibility using a PropertyChangeListener. 
     * @return
     */
    public PNode getEFieldNode() {
        return eFieldNode;
    }
    
    private void updateGeometry() {
        
        // model-to-view transform
        Capacitor capacitor = circuit.getCapacitor();
        final double plateSize = capacitor.getPlateSideLength();
        final double plateThickness = capacitor.getPlateThickness();
        final double plateSeparation = capacitor.getPlateSeparation();
        final double dielectricHeight = capacitor.getDielectricHeight();
        
        // geometry
        topPlateNode.setSize( plateSize, plateThickness, plateSize );
        bottomPlateNode.setSize( plateSize, plateThickness, plateSize );
        dielectricNode.setSize( plateSize, dielectricHeight, plateSize );
        
        // layout nodes with zero dielectric offset
        double x = 0;
        double y = -( plateSeparation / 2 ) - plateThickness;
        double z = 0;
        topPlateNode.setOffset( mvt.modelToViewDelta( x, y, z ) );
        y = -dielectricHeight / 2;
        dielectricNode.setOffset( mvt.modelToViewDelta( x, y, z ) );
        y = plateSeparation / 2;
        bottomPlateNode.setOffset( mvt.modelToViewDelta( x, y, z ) );

        // adjust the dielectric offset
        updateDielectricOffset();
    }
    
    private void updateDielectricOffset() {
        double x = circuit.getCapacitor().getDielectricOffset();
        double y = -circuit.getCapacitor().getDielectricHeight() / 2;
        double z = 0;
        dielectricNode.setOffset( mvt.modelToViewDelta( x, y, z ) );
    }
    
    private void updateDielectricColor() {
        dielectricNode.setColor( circuit.getCapacitor().getDielectricMaterial().getColor() );
    }
    
    public interface CapacitorNodeChangeListener extends EventListener {
        public void plateChargeVisibleChanged();
        public void eFieldVisibleChanged();
        public void dielectricChargeViewChanged();
    }
    
    public static class CapacitorNodeChangeAdapter implements CapacitorNodeChangeListener {
        public void plateChargeVisibleChanged() {}
        public void eFieldVisibleChanged() {}
        public void dielectricChargeViewChanged() {}
    }
    
    public void addCapacitorNodeChangeListener( CapacitorNodeChangeListener listener ) {
        listeners.add( CapacitorNodeChangeListener.class, listener );
    }
    
    public void removeCapacitorNodeChangeListener( CapacitorNodeChangeListener listener ) {
        listeners.remove( CapacitorNodeChangeListener.class, listener );
    }
    
    private void firePlateChargeVisibleChanged() {
        for ( CapacitorNodeChangeListener listener : listeners.getListeners( CapacitorNodeChangeListener.class ) ) {
            listener.plateChargeVisibleChanged();
        }
    }
    
    private void fireEFieldVisibleChanged() {
        for ( CapacitorNodeChangeListener listener : listeners.getListeners( CapacitorNodeChangeListener.class ) ) {
            listener.eFieldVisibleChanged();
        }
    }
    
    private void fireDielectricChargeViewChanged() {
        for ( CapacitorNodeChangeListener listener : listeners.getListeners( CapacitorNodeChangeListener.class ) ) {
            listener.dielectricChargeViewChanged();
        }
    }
}
