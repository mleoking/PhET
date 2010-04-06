/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Depicts the ratio of concentrations (HA/A, H3O/OH) as images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ImagesNode extends PComposite {
    
    private static final double BASE_CONCENTRATION = 1E-7;
    private static final int BASE_DOTS = 2;

    // if we have fewer than this number of dots, cheat them towards the center of the bounds
    private static final int COUNT_THRESHOLD_FOR_ADJUSTING_BOUNDS = 20;
    
    private final WeakAcid solution;
    private final PNode containerNode;
    private final PNode parentHA, parentA, parentH3O, parentOH;
    private final Random randomCoordinate;
    private final EventListenerList listeners;
    
    private int maxImages = MGPConstants.MAX_IMAGES_RANGE.getDefault();
    private double imageScale = MGPConstants.IMAGE_SCALE_RANGE.getDefault();
    private float imageTransparency = (float) MGPConstants.IMAGE_TRANSPARENCY_RANGE.getDefault();
    private int countHA, countA, countH3O, countOH;
    
    public ImagesNode( final WeakAcid solution, PNode containerNode ) {
        super();
        setPickable( false );
        
        listeners = new EventListenerList();

        this.containerNode = containerNode;
        containerNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    deleteAllImages();
                    updateNumberOfImages();
                }
            }
        });
        
        this.solution = solution;
        solution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateNumberOfImages();
            }
        });
        
        randomCoordinate = new Random();
        
        parentHA = new PComposite();
        parentA = new PComposite();
        parentH3O = new PComposite();
        parentOH = new PComposite();
        
        // rendering order will be modified later based on number of dots
        addChild( parentHA );
        addChild( parentA );
        addChild( parentH3O );
        addChild( parentOH );
        
        updateNumberOfImages();
    }
    
    public void setMaxImages( int maxImages ) {
        if ( maxImages != this.maxImages ) {
            this.maxImages = maxImages;
            updateNumberOfImages();
            fireStateChanged();
        }
    }
    
    public int getMaxImages() {
        return maxImages;
    }
    
    public void setImageScale( double imageScale ) {
        if ( imageScale != this.imageScale ) {
            this.imageScale = imageScale;
            updateScale();
            fireStateChanged();
        }
    }
    
    public double getImageScale() {
        return imageScale;
    }
    
    public void setImageTransparency( float imageTransparency ) {
        if ( imageTransparency != this.imageTransparency ) {
            this.imageTransparency = imageTransparency;
            updateTransparency();
            fireStateChanged();
        }
    }
    
    public float getImageTransparency() {
        return imageTransparency;
    }
    
    public int getCountHA() {
        return countHA;
    }
    
    public int getCountA() {
        return countA;
    }
    
    public int getCountH3O() {
        return countH3O;
    }
    
    public int getCountOH() {
        return countOH;
    }

    /*
     * Creates images based on the current pH value.
     * Images are spread at random location throughout the container.
     */
    private void updateNumberOfImages() {

        countHA = getNumberOfImages( solution.getConcentrationHA() );
        countA = getNumberOfImages( solution.getConcentrationA() );
        countH3O = getNumberOfImages( solution.getConcentrationH3O() );
        countOH = getNumberOfImages( solution.getConcentrationOH() );
        
        adjustNodeCount( countHA, MGPConstants.HA_IMAGE, parentHA );
        adjustNodeCount( countA, MGPConstants.A_MINUS_IMAGE, parentA );
        adjustNodeCount( countH3O, MGPConstants.H3O_PLUS_IMAGE, parentH3O );
        adjustNodeCount( countOH, MGPConstants.OH_MINUS_IMAGE, parentOH );

        sortDots();
    }
    
    private void deleteAllImages() {
        parentHA.removeAllChildren();
        parentA.removeAllChildren();
        parentH3O.removeAllChildren();
        parentOH.removeAllChildren();
        countHA = countA = countH3O = countOH = 0;
    }
    
    // Changes the rendering order from most to least dots.
    private void sortDots() {
        PNode[] dotParents = new PNode[] { parentHA, parentA, parentH3O, parentOH };
        Arrays.sort( dotParents, new ChildrenCountComparator() );
        for ( int i = 0; i < dotParents.length; i++ ) {
            dotParents[i].moveToBack();
        }
    }
    
    // Number of images is based on concentration.
    private int getNumberOfImages( double concentration ) {
        final double raiseFactor = MathUtil.log10( concentration / BASE_CONCENTRATION );
        final double baseFactor = Math.pow( ( maxImages / BASE_DOTS ), ( 1 / MathUtil.log10( 1 / BASE_CONCENTRATION ) ) );
        return (int)( BASE_DOTS * Math.pow( baseFactor, raiseFactor ) );
    }
    
    // Adjusts the number of images, creates images at random locations.
    private void adjustNodeCount( int count, Image image, PNode parent ) {

        // remove nodes
        while ( count < parent.getChildrenCount() && count > 0 ) {
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
    }
    
    /*
     * Gets the bounds within which a dot will be created.
     * This is typically the container bounds.
     * But if the number of dots is small, we shrink the container bounds so 
     * that dots stay away from the edges, making them easier to see.
     */
    private PBounds getContainerBounds( int count ) {
        PBounds bounds = containerNode.getFullBoundsReference();
        if ( count < COUNT_THRESHOLD_FOR_ADJUSTING_BOUNDS ) {
            double margin = 10;
            bounds = new PBounds( bounds.getX() + margin, bounds.getY() + margin, bounds.getWidth() - ( 2 * margin ), bounds.getHeight() - ( 2 * margin ) );
        }
        return bounds;
    }

    // Gets a random point inside some bounds.
    private void getRandomPoint( PBounds bounds, Point2D pOutput ) {
        double x = bounds.getX() + ( randomCoordinate.nextDouble() * bounds.getWidth() );
        double y = bounds.getY() + ( randomCoordinate.nextDouble() * bounds.getHeight() );
        pOutput.setLocation( x, y );
    }
    
    private void updateScale() {
        updateScale( parentHA, imageScale );
        updateScale( parentA, imageScale );
        updateScale( parentH3O, imageScale );
        updateScale( parentOH, imageScale );
    }
    
    private static void updateScale( PNode parent, double scale ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof ImageNode ) {
                ( (ImageNode) child ).setScale( scale );
            }
        }
    }
    
    private void updateTransparency() {
        updateTransparency( parentHA, imageTransparency );
        updateTransparency( parentA, imageTransparency );
        updateTransparency( parentH3O, imageTransparency );
        updateTransparency( parentOH, imageTransparency );
    }
    
    private static void updateTransparency( PNode parent, float transparency ) {
        for ( int i = 0; i < parent.getChildrenCount(); i++ ) {
            PNode child = parent.getChild( i );
            if ( child instanceof ImageNode ) {
                ( (ImageNode) child ).setTransparency( transparency );
            }
        }
    }
    
    // marker class for molecule image nodes
    private static class ImageNode extends PImage {
        public ImageNode( Image image, double scale, float transparency ) {
            super( image );
            setScale( scale );
            setTransparency( transparency );
        }
    }
    
    // Sorts PNodes based on how many children they have (least to most).
    private static class ChildrenCountComparator implements Comparator<PNode> {

        public int compare( final PNode node1, final PNode node2 ) {
            final int count1 = node1.getChildrenCount();
            final int count2 = node2.getChildrenCount();
            if ( count1 > count2 ) {
                return 1;
            }
            else if ( count1 < count2 ) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}
