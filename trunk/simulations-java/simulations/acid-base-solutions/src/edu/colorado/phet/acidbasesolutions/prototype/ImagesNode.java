/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Image;

import edu.colorado.phet.acidbasesolutions.prototype.IMoleculeCountStrategy.ConcentrationMoleculeCountStrategy;
import edu.colorado.phet.acidbasesolutions.prototype.IMoleculeCountStrategy.ConstantMoleculeCountStrategy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Represents concentration ratios (HA/A, H3O/OH) as images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ImagesNode extends MoleculesNode {
    
    private double imageScale;
    private final PNode h2oParentBack, h2oParentMiddle, h2oParentFront;
    
    public ImagesNode( final WeakAcid solution, MagnifyingGlass magnifyingGlass, boolean showOH ) {
        super( solution, magnifyingGlass, MGPConstants.MAX_IMAGES_RANGE.getDefault(), MGPConstants.MAX_H2O_IMAGES_RANGE.getDefault(), 
                (float) MGPConstants.IMAGE_TRANSPARENCY_RANGE.getDefault(), new ConcentrationMoleculeCountStrategy(), new ConstantMoleculeCountStrategy(), showOH );
        imageScale = MGPConstants.IMAGE_SCALE_RANGE.getDefault();
        
        // add parents for H2O layers
        h2oParentBack = new PComposite();
        h2oParentMiddle = new PComposite();
        h2oParentFront = new PComposite();
        
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
        if ( parent == getParentH2O() ) {
            updateTransparency( h2oParentBack, getTransparencyBack() );
            updateTransparency( h2oParentMiddle, getTransparencyMiddle() );
            updateTransparency( h2oParentFront, getTransparencyFront() );
        }
        else {
            for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
                PNode child = parent.getChild( i );
                if ( child instanceof ImageNode ) {
                    ( (ImageNode) child ).setTransparency( transparency );
                }
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
        updateNumberOfH2ONodes();
    }
    
    // Adjusts the number of images, creates images at random locations.
    private void updateNumberOfMoleculeNodes( PNode parent, int count, double scale, float transparency, Image image ) {

        //HACK: add these because they are removed by super.deleteAllMolecules
        getParentH2O().addChild( h2oParentBack );
        getParentH2O().addChild( h2oParentMiddle );
        getParentH2O().addChild( h2oParentFront );
        
        // remove nodes
        while ( count < parent.getChildrenCount() && count >= 0 ) {
            parent.removeChild( parent.getChildrenCount() - 1 );
        }

        // add nodes
        while ( count > parent.getChildrenCount() ) {
            ImageNode node = new ImageNode( image, scale, transparency );
            node.setOffset( getRandomPoint() );
            parent.addChild( node );
        }

        assert( count == parent.getChildrenCount() );
    }
    
    // Adjusts the number of H2O images, which are represented as 3 layers.
    private void updateNumberOfH2ONodes() {
       
        // counts
        int count = getCountH2O();
        int countBack = (int)( 0.75 * count );  // most of the molecules are in back layer
        int countMiddle = (int)( 0.75 * ( count - countBack ) );  // most of remaining molecules are in middle layer
        int countFront = count - countBack - countMiddle; // whatever is left over goes in front layer
        
        // update images
        updateNumberOfMoleculeNodes( h2oParentBack, countBack, imageScale, getTransparencyBack(), MGPConstants.H2O_IMAGE );
        updateNumberOfMoleculeNodes( h2oParentMiddle, countMiddle, imageScale, getTransparencyMiddle(), MGPConstants.H2O_IMAGE );
        updateNumberOfMoleculeNodes( h2oParentFront, countFront, imageScale, getTransparencyFront(), MGPConstants.H2O_IMAGE );
    }
    
    private float getTransparencyBack() {
        return 0.2f * getH2OTransparency();
    }
    
    private float getTransparencyMiddle() {
        return 0.5f * getH2OTransparency();
    }
    
    private float getTransparencyFront() {
        return getH2OTransparency();
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
