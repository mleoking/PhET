/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.common.piccolophet.PhetPNode;


public class CapacitorNode extends PhetPNode {

    private final Capacitor capacitor;
    private final ModelViewTransform mvt;
    private final PlateNode topPlateNode, bottomPlateNode;
    private final DielectricNode dielectricNode;
    
    public CapacitorNode( Capacitor capacitor, ModelViewTransform mvt ) {
        
        this.capacitor = capacitor;
        this.capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {

            public void dielectricMaterialChanged() {
                updateDielectricColor();
            }

            public void dielectricOffsetChanged() {
                updateDielectricOffset();
            }

            public void plateSeparationChanged() {
                updateGeometry();
            }

            public void plateSizeChanged() {
                updateGeometry();
            }
        });
        
        this.mvt = mvt;
        
        topPlateNode = new PlateNode();
        bottomPlateNode = new PlateNode();
        dielectricNode = new DielectricNode( capacitor.getDielectricMaterial().getColor() );
        
        // rendering order
        addChild( bottomPlateNode );
        addChild( dielectricNode ); // dielectric between the plates
        addChild( topPlateNode );
        
        // default state
        updateGeometry();
        updateDielectricColor();
    }
    
    private void updateGeometry() {
        
        // model-to-view transform
        double plateSize = mvt.modelToView( capacitor.getPlateSize() );
        double plateThickness = mvt.modelToView( capacitor.getPlateThickness() );
        double plateSeparation = mvt.modelToView( capacitor.getPlateSeparation() );
        
        // geometry
        topPlateNode.setShape( plateSize, plateSize, plateThickness );
        bottomPlateNode.setShape( plateSize, plateSize, plateThickness );
        dielectricNode.setShape( plateSize, plateSize, plateSeparation );
        
        // layout
        double x = 0;
        double y = 0;
        topPlateNode.setOffset( x, y );
        x = topPlateNode.getXOffset();
        y = topPlateNode.getYOffset() + plateThickness + plateSeparation;
        bottomPlateNode.setOffset( x, y );
        updateDielectricOffset();
    }
    
    private void updateDielectricOffset() {
        double dielectricOffset = mvt.modelToView( capacitor.getDielectricOffset() );
        double plateThickness = mvt.modelToView( capacitor.getPlateThickness() );
        double x = topPlateNode.getXOffset() + dielectricOffset;
        double y = topPlateNode.getYOffset() + plateThickness;
        dielectricNode.setOffset( x, y );
    }
    
    private void updateDielectricColor() {
        dielectricNode.setColor( capacitor.getDielectricMaterial().getColor() );
    }
}
