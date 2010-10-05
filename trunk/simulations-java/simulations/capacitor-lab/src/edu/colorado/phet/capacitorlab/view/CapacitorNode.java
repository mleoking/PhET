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
     * Controls the opacity of the physical parts of the capacitor.
     * This is needed because the plates and dielectric must be transparent
     * when certain nodes (voltmeter, E-Field detector, E-field view) are visible,
     * in order to see what's doing on inside the capacitor.
     * @param opaque
     */
    public void setOpaque( boolean opaque ) {
        float transparency = ( opaque ) ? 1f : TRANSPARENCY;
        topPlateNode.setTransparency( transparency );
        dielectricNode.setTransparency( transparency );
        bottomPlateNode.setTransparency( transparency );
    }
    
    public void setDielectricVisible( boolean visible ) {
        dielectricNode.setVisible( visible );
    }
    
    public void setPlateChargeVisible( boolean visible ) {
        topPlateNode.setChargeVisible( visible );
        bottomPlateNode.setChargeVisible( visible );
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
        eFieldNode.setVisible( visible );
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
        double y = mvt.modelToView( -( plateSeparation / 2 ) - plateThickness );
        topPlateNode.setOffset( x, y );
        y = mvt.modelToView( -dielectricHeight / 2 );
        dielectricNode.setOffset( x, y );
        y = mvt.modelToView( plateSeparation / 2 );
        bottomPlateNode.setOffset( x, y );

        // adjust the dielectric offset
        updateDielectricOffset();
    }
    
    private void updateDielectricOffset() {
        double x = mvt.modelToView( circuit.getCapacitor().getDielectricOffset() );
        double y = dielectricNode.getYOffset();
        dielectricNode.setOffset( x, y );
    }
    
    private void updateDielectricColor() {
        dielectricNode.setColor( circuit.getCapacitor().getDielectricMaterial().getColor() );
    }
    
    public interface CapacitorNodeChangeListener extends EventListener {
        public void dielectricChargeViewChanged();
    }
    
    public void addCapacitorNodeChangeListener( CapacitorNodeChangeListener listener ) {
        listeners.add( CapacitorNodeChangeListener.class, listener );
    }
    
    public void removeCapacitorNodeChangeListener( CapacitorNodeChangeListener listener ) {
        listeners.remove( CapacitorNodeChangeListener.class, listener );
    }
    
    private void fireDielectricChargeViewChanged() {
        for ( CapacitorNodeChangeListener listener : listeners.getListeners( CapacitorNodeChangeListener.class ) ) {
            listener.dielectricChargeViewChanged();
        }
    }
}
