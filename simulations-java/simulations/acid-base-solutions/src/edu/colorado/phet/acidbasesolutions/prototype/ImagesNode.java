/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Image;
import java.awt.geom.Point2D;

import edu.colorado.phet.acidbasesolutions.prototype.IMoleculeCountStrategy.ConcentrationMoleculeCountStrategy;
import edu.colorado.phet.acidbasesolutions.prototype.IMoleculeCountStrategy.ConstantMoleculeCountStrategy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Represents concentration ratios (HA/A, H3O/OH) as images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ImagesNode extends MoleculesNode {
    
    private double imageScale;
    
    public ImagesNode( final WeakAcid solution, PNode containerNode ) {
        super( solution, containerNode, MGPConstants.MAX_IMAGES_RANGE.getDefault(), MGPConstants.MAX_H2O_IMAGES_RANGE.getDefault(), 
                (float) MGPConstants.IMAGE_TRANSPARENCY_RANGE.getDefault(), new ConcentrationMoleculeCountStrategy(), new ConstantMoleculeCountStrategy() );
        imageScale = MGPConstants.IMAGE_SCALE_RANGE.getDefault();
        updateNumberOfMoleculeNodes(); // call this last
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
        updateNumberOfMoleculeNodes( getParentHA(), getCountHA(), imageScale, getTransparency(), MGPConstants.HA_IMAGE );
        updateNumberOfMoleculeNodes( getParentA(), getCountA(), imageScale, getTransparency(), MGPConstants.A_MINUS_IMAGE );
        updateNumberOfMoleculeNodes( getParentH3O(), getCountH3O(), imageScale, getTransparency(), MGPConstants.H3O_PLUS_IMAGE );
        updateNumberOfMoleculeNodes( getParentOH(), getCountOH(), imageScale, getTransparency(), MGPConstants.OH_MINUS_IMAGE );
        updateNumberOfMoleculeNodes( getParentH2O(), getCountH2O(), imageScale, getH2OTransparency(), MGPConstants.H2O_IMAGE );
    }
    
    // Adjusts the number of images, creates images at random locations.
    private void updateNumberOfMoleculeNodes( PNode parent, int count, double scale, float transparency, Image image ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count >= 0 ) {
            parent.removeChild( parent.getChildrenCount() - 1 );
        }

        // add nodes
        Point2D pOffset = new Point2D.Double();
        PBounds bounds = getContainerBounds( count );
        while ( count > parent.getChildrenCount() ) {
            getRandomPoint( bounds, pOffset );
            ImageNode p = new ImageNode( image, scale, transparency );
            p.setOffset( pOffset );
            parent.addChild( p );
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
