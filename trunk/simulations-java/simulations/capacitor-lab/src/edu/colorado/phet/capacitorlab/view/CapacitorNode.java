/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.view.DielectricNode.DielectricChargeView;
import edu.colorado.phet.capacitorlab.view.PlateNode.BottomPlateNode;
import edu.colorado.phet.capacitorlab.view.PlateNode.TopPlateNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Visual representation of a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorNode extends PhetPNode {

    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final PlateNode topPlateNode, bottomPlateNode;
    private final DielectricNode dielectricNode;
    
    public CapacitorNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
        
        this.circuit = circuit;
        this.circuit.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {

            @Override
            public void dielectricMaterialChanged() {
                updateDielectricColor();
            }

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
        
        // child nodes
        topPlateNode = new TopPlateNode( circuit, mvt, dev );
        bottomPlateNode = new BottomPlateNode( circuit, mvt, dev );
        dielectricNode = new DielectricNode( circuit.getCapacitor(), mvt, CLConstants.DIELECTRIC_OFFSET_RANGE );
        
        // rendering order
        addChild( bottomPlateNode );
        addChild( dielectricNode ); // dielectric between the plates
        addChild( topPlateNode );
        
        // default state
        updateGeometry();
        updateDielectricColor();
    }
    
    public void setPlateChargeVisible( boolean visible ) {
        topPlateNode.setChargeVisible( visible );
        bottomPlateNode.setChargeVisible( visible );
    }
    
    public boolean isPlateChargeVisible() {
        return topPlateNode.isChargeVisible();
    }
    
    public void setDielectricChargeView( DielectricChargeView view ) {
        dielectricNode.setDielectricChargeView( view );
    }
    
    public DielectricChargeView getDielectricChargeView() {
        return dielectricNode.getDielectricChargeView();
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
}
