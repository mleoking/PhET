/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Image;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Represents concentration ratios (HA/A, H3O/OH) as images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ImagesNode extends MoleculesNode {
    
    private double imageScale = MGPConstants.IMAGE_SCALE_RANGE.getDefault();
    private float imageTransparency = (float) MGPConstants.IMAGE_TRANSPARENCY_RANGE.getDefault();
    
    public ImagesNode( final WeakAcid solution, PNode containerNode ) {
        super( solution, containerNode, MGPConstants.MAX_IMAGES_RANGE.getDefault() );
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
                    setScale( parent, imageScale );
                }
            }
            fireStateChanged();
        }
    }
    
    private static void setScale( PNode parent, double scale ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof ImageNode ) {
                ( (ImageNode) child ).setScale( scale );
            }
        }
    }
    
    public float getImageTransparency() {
        return imageTransparency;
    }
    
    public void setImageTransparency( float imageTransparency ) {
        if ( imageTransparency != this.imageTransparency ) {
            this.imageTransparency = imageTransparency;
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                PNode parent = getChild( i );
                if ( parent instanceof MoleculeParentNode ) {
                   setTransparency( parent, imageTransparency );
                }
            }
            fireStateChanged();
        }
    }
    
    private static void setTransparency( PNode parent, float transparency ) {
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
        updateNumberOfMoleculeNodes( getParentHA(), getCountHA(), MGPConstants.HA_IMAGE );
        updateNumberOfMoleculeNodes( getParentA(), getCountA(), MGPConstants.A_MINUS_IMAGE );
        updateNumberOfMoleculeNodes( getParentH3O(), getCountH3O(), MGPConstants.H3O_PLUS_IMAGE );
        updateNumberOfMoleculeNodes( getParentOH(), getCountOH(), MGPConstants.OH_MINUS_IMAGE );
    }
    
    // Adjusts the number of images, creates images at random locations.
    private void updateNumberOfMoleculeNodes( PNode parent, int count, Image image ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count >= 0 ) {
            parent.removeChild( parent.getChildrenCount() - 1 );
        }

        // add nodes
        Point2D pOffset = new Point2D.Double();
        PBounds bounds = getContainerBounds( count );
        while ( count > parent.getChildrenCount() ) {
            getRandomPoint( bounds, pOffset );
            ImageNode p = new ImageNode( image, imageScale, imageTransparency );
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
