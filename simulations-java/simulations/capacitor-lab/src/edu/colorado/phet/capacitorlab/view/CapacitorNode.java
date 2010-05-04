/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;


public class CapacitorNode extends PhetPNode {

    private final Capacitor capacitor;
    private final ModelViewTransform mvt;
    private final PNode parentNode;
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
        
        parentNode = new PNode();
        topPlateNode = new PlateNode();
        bottomPlateNode = new PlateNode();
        dielectricNode = new DielectricNode( capacitor.getDielectricMaterial().getColor() );
        
        // rendering order
        addChild( parentNode );
        parentNode.addChild( bottomPlateNode );
        parentNode.addChild( dielectricNode ); // dielectric between the plates
        parentNode.addChild( topPlateNode );
        
        // default state
        updateGeometry();
        updateDielectricColor();
    }
    
    private void updateGeometry() {
        
        // model-to-view transform
        double plateSize = mvt.modelToView( capacitor.getPlateSize() );
        double plateThickness = mvt.modelToView( capacitor.getPlateThickness() );
        double plateSeparation = mvt.modelToView( capacitor.getPlateSeparation() );
        double dielectricGap = mvt.modelToView( capacitor.getDielectricGap() );
        
        // geometry
        topPlateNode.setShape( plateSize, plateSize, plateThickness );
        bottomPlateNode.setShape( plateSize, plateSize, plateThickness );
        dielectricNode.setShape( plateSize, plateSize, plateSeparation - ( 2 * dielectricGap ) );
        
        // layout nodes with zero dielectric offset
        double x = 0;
        double y = 0;
        topPlateNode.setOffset( x, y );
        x = topPlateNode.getXOffset();
        y = topPlateNode.getYOffset() + plateThickness + plateSeparation;
        bottomPlateNode.setOffset( x, y );
        x = topPlateNode.getXOffset();
        y = topPlateNode.getYOffset() + plateThickness + dielectricGap;
        dielectricNode.setOffset( x, y );
        
        // move the origin to the geometric center
        x = -( parentNode.getFullBoundsReference().getWidth() / 2 ) - PNodeLayoutUtils.getOriginXOffset( parentNode );
        y = -( parentNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( parentNode );
        parentNode.setOffset( x, y );
        
        // adjust the dielectric offset
        updateDielectricOffset();
    }
    
    private void updateDielectricOffset() {
        
        // model-to-view transform
        double dielectricOffset = mvt.modelToView( capacitor.getDielectricOffset() );
        double plateThickness = mvt.modelToView( capacitor.getPlateThickness() );
        double dielectricGap = mvt.modelToView( capacitor.getDielectricGap() );
        
        // layout
        double x = topPlateNode.getXOffset() + dielectricOffset;
        double y = topPlateNode.getYOffset() + plateThickness + dielectricGap;
        dielectricNode.setOffset( x, y );
    }
    
    private void updateDielectricColor() {
        dielectricNode.setColor( capacitor.getDielectricMaterial().getColor() );
    }
}
