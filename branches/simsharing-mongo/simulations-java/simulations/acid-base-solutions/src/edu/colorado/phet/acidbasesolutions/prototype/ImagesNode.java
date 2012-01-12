// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Image;
import java.awt.geom.Point2D;

import edu.colorado.phet.acidbasesolutions.prototype.IMoleculeCountStrategy.ConcentrationMoleculeCountStrategy;
import edu.colorado.phet.acidbasesolutions.prototype.IMoleculeCountStrategy.ConstantMoleculeCountStrategy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Represents concentration ratios (HA/A, H3O/OH) as images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ImagesNode extends MoleculesNode {
    
    private double imageScale;
    
    public ImagesNode( final WeakAcid solution, MagnifyingGlass magnifyingGlass, boolean showOH ) {
        super( solution, magnifyingGlass, MGPConstants.MAX_IMAGES_RANGE.getDefault(), MGPConstants.MAX_H2O_IMAGES_RANGE.getDefault(), 
                (float) MGPConstants.IMAGE_TRANSPARENCY_RANGE.getDefault(), new ConcentrationMoleculeCountStrategy(), new ConstantMoleculeCountStrategy(), showOH );
        imageScale = MGPConstants.IMAGE_SCALE_RANGE.getDefault();
        updateNumberOfMolecules(); // call this last
    }
    
    public double getImageScale() {
        return imageScale;
    }
    
    public void setImageScale( double imageScale ) {
        if ( imageScale != this.imageScale ) {
            this.imageScale = imageScale;
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                PNode parent = getChild( i );
                if ( parent instanceof MoleculeParentNode ) {
                    updateScale( parent, imageScale );
                }
            }
            fireStateChanged();
        }
    }
    
    // Updates the scale of existing ImageNodes that are children of parent.
    private static void updateScale( PNode parent, double scale ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof ImageNode ) {
                ( (ImageNode) child ).setScale( scale );
            }
        }
    }
    
    // Updates the transparency of existing ImageNodes that are children of parent.
    protected void updateTransparency( PNode parent, float transparency ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof ImageNode ) {
                ( (ImageNode) child ).setTransparency( transparency );
            }
        }
    }
    
    /*
     * Creates images based on the current pH value.
     * Images are spread at random location throughout the container.
     */
    protected void updateNumberOfMoleculeNodes() {
        updateNumberOfMoleculeNodes( getParentHA(), getCountHA(), imageScale, getMoleculeTransparency(), MGPConstants.HA_IMAGE );
        updateNumberOfMoleculeNodes( getParentA(), getCountA(), imageScale, getMoleculeTransparency(), MGPConstants.A_MINUS_IMAGE );
        updateNumberOfMoleculeNodes( getParentH3O(), getCountH3O(), imageScale, getMoleculeTransparency(), MGPConstants.H3O_PLUS_IMAGE );
        updateNumberOfMoleculeNodes( getParentOH(), getCountOH(), imageScale, getMoleculeTransparency(), MGPConstants.OH_MINUS_IMAGE );
        updateNumberOfMoleculeNodes( getParentH2O(), getCountH2O(), imageScale, getMoleculeTransparency(), MGPConstants.H2O_IMAGE );
    }
    
    private void updateNumberOfMoleculeNodes( PNode parent, int count, double scale, float transparency, Image image ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count >= 0 ) {
            parent.removeChild( parent.getChildrenCount() - 1 );
        }

        // add nodes
        while ( count > parent.getChildrenCount() ) {
            ImageNode node = new ImageNode( image, scale, transparency );
            Point2D p = getRandomPoint();
            double x = p.getX() - ( node.getFullBoundsReference().getWidth() / 2 );
            double y = p.getY() - ( node.getFullBoundsReference().getHeight() / 2 );
            node.setOffset( x, y );
            parent.addChild( node );
        }

        assert( count == parent.getChildrenCount() );
    }
    
    // Molecule image node
    private static class ImageNode extends PImage {
        public ImageNode( Image image, double scale, float transparency ) {
            super( image );
            setScale( scale );
            setTransparency( transparency );
        }
    }
}
