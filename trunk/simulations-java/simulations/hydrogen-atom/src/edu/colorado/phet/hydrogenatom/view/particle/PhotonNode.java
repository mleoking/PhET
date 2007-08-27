/* Copyright 2006-2007, University of Colorado */

package edu.colorado.phet.hydrogenatom.view.particle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.Photon;
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.OriginNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhotonImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * PhotonNode is the visual representation of a photon.
 * Photon images are cached to minimize memory requirements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhotonNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    /* enable debug output for the image cache */
    private static final boolean DEBUG_CACHE_ENABLED = false;
    
    /* draws an outline around the full bonds of the node */
    private static final boolean DEBUG_BOUNDS = false;
    
    /* adds an arrow to the node showing the orientation */
    private static final boolean DEBUG_ORIENTATION = false;

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------

    // public because it's used by collision detection code in the model
    public static final double DIAMETER = 30;
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------

    // Image cache, shared by all instances
    private static final Integer UV_IMAGE_KEY = new Integer( (int)( VisibleColor.MIN_WAVELENGTH - 1 ) );
    private static final Integer IR_IMAGE_KEY = new Integer( (int)( VisibleColor.MAX_WAVELENGTH + 1 ) );
    private static final ImageCache IMAGE_CACHE = new ImageCache();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Photon _photon; // model element
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param photon
     */
    public PhotonNode( Photon photon ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        Image image = lookupPhotonImage( photon.getWavelength() );
        PImage imageNode = new PImage( image );
        addChild( imageNode );
        
        // Move origin to center
        imageNode.setOffset( -imageNode.getFullBounds().getWidth()/2, -imageNode.getFullBounds().getHeight()/2 );
        
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            // puts a dot at the origin
            OriginNode originNode = new OriginNode( Color.BLACK );
            addChild( originNode );
        }
        
        if ( DEBUG_BOUNDS ) {
            // puts a rectangle around the node's bounds
            PPath diameterNode = new PPath( new Rectangle.Double( -DIAMETER/2, -DIAMETER/2, DIAMETER, DIAMETER ) );
            diameterNode.setStroke( new BasicStroke( 1f ) );
            diameterNode.setStrokePaint( Color.WHITE );
            addChild( diameterNode );
        }
        
        if ( DEBUG_ORIENTATION ) {
            // puts an arrow on the node, showing orientation
            Point2D pTail = new Point2D.Double( -DIAMETER/3, 0 );
            Point2D pHead = new Point2D.Double( DIAMETER/3, 0 );
            double headHeight = DIAMETER / 4;
            double headWidth = DIAMETER / 2;
            double tailWidth = DIAMETER / 8;
            PPath arrowNode = new ArrowNode( pTail, pHead, headHeight, headWidth, tailWidth );
            arrowNode.setPaint( photon.getColor() );
            addChild( arrowNode );
        }
        
        _photon = photon;
        _photon.addObserver( this );
    }
    
    /*
     * Gets the photon image from the cache.
     * If we don't have an image for the specified wavelength,
     * create one and add it to the cache.
     */
    private static final Image lookupPhotonImage( double wavelength ) {
        Image image = IMAGE_CACHE.get( wavelength );
        if ( image == null ) {
            image = createPhotonImage( wavelength );
            IMAGE_CACHE.put( wavelength, image );
        }     
        return image;
    }

    /**
     * Creates a photon image.
     *
     * @param wavelength the wavelength in nanometers
     * @return the photon image
     */
    public static Image createPhotonImage( double wavelength ) {
        return PhotonImageFactory.createPhotonImage( wavelength, DIAMETER );
    }

    //----------------------------------------------------------------------------
    // Image cache
    //----------------------------------------------------------------------------
    
    /*
     * Cache that maps wavelengths to images.
     * The mapping is done with integer precision.
     */
    private static class ImageCache {

        private HashMap _map; // key=Integer, value=Image

        public ImageCache() {
            _map = new HashMap();
        }

        /**
         * Puts an image in the cache.
         * @param wavelength
         * @param image
         */
        public void put( double wavelength, Image image ) {
            Object key = wavelengthToKey( wavelength );
            _map.put( key, image );
            if ( DEBUG_CACHE_ENABLED ) {
                System.out.println( "PhotonNode.ImageCache.put size=" + _map.size() );
            }
        }

        /**
         * Gets an image from the cache.
         * @param wavelength
         * @return Image, possibly null
         */
        public Image get( double wavelength ) {
            Object key = wavelengthToKey( wavelength );
            return (Image) _map.get( key );
        }
        
        /*
         * Converts a wavelength to a key.
         * Visible wavelengths are mapped with integer precision.
         * All UV wavelengths map to the same key, ditto for IR.
         */
        private Object wavelengthToKey( double wavelength ) {
            Object key = null;
            if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
                key = UV_IMAGE_KEY;
            }
            else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
                key = IR_IMAGE_KEY;
            }
            else {
                key = new Integer( (int) wavelength );
            }
            return key;
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update( Observable o, Object arg ) {
        update();
    }
    
    private void update() {
        setRotation( _photon.getOrientation() );
        setOffset( ModelViewTransform.transform( _photon.getPositionRef() ) );
    }
}
