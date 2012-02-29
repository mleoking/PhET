// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes.photon;

import java.awt.Image;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * Caches images that represent photons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhotonImageCache {

    /* enable debug output for the image cache */
    private static final boolean DEBUG_CACHE_ENABLED = false;

    // Image cache
    private static final Integer UV_IMAGE_KEY = new Integer( (int) ( VisibleColor.MIN_WAVELENGTH - 1 ) );
    private static final Integer IR_IMAGE_KEY = new Integer( (int) ( VisibleColor.MAX_WAVELENGTH + 1 ) );
    private static final ImageCache IMAGE_CACHE = new ImageCache();


    /* not intended for instantiation */
    private PhotonImageCache() {}

    /**
     * Gets a photon image from the cache. If we don't have an image for the specified
     * wavelength and diameter, create one and add it to the cache. Use caution not to
     * modify the returned image; if you need to modify it, make a copy of the image,
     * or use PhotonNode.createImage.
     *
     * @param wavelength
     * @param diameter
     * @return Image
     */
    public static final Image getImage( double wavelength, double diameter ) {
        Image image = IMAGE_CACHE.get( wavelength, diameter );
        if ( image == null ) {
            image = PhotonNode.createImage( wavelength, diameter );
            IMAGE_CACHE.put( wavelength, diameter, image );
        }
        return image;
    }

    /**
     * Clears the cache.
     */
    public static final void clear() {
        IMAGE_CACHE.clear();
    }

    //TODO change this to use a single map and generics, see #2620
    /*
    * Cache that maps wavelengths and diameters to images.
    * A double mapping is involved.
    * Diameter maps to a HashMap, which then maps wavelength to an Image.
    * Mapping of wavelength is done with integer precision.
    */
    private static class ImageCache {

        private HashMap _diameterMap; // key=diameter (Double), value=HashMap

        public ImageCache() {
            _diameterMap = new HashMap();
        }

        public void clear() {
            _diameterMap.clear();
        }

        /**
         * Puts an image in the cache.
         *
         * @param wavelength
         * @param diameter
         * @param image
         */
        public void put( double wavelength, double diameter, Image image ) {
            // find the map for this diameter
            Object diameterKey = diameterToKey( diameter );
            HashMap wavelengthMap = (HashMap) _diameterMap.get( diameterKey );
            if ( wavelengthMap == null ) {
                // no map for this diameter, so create one
                wavelengthMap = new HashMap();
                _diameterMap.put( diameterKey, wavelengthMap );
            }
            Object wavelengthKey = wavelengthToKey( wavelength );
            wavelengthMap.put( wavelengthKey, image );
            if ( DEBUG_CACHE_ENABLED ) {
                System.out.println( "PhotonImageFactory.ImageCache.put diameterMap.size=" + _diameterMap.size() + " wavelengthMap.size=" + wavelengthMap.size() );
            }
        }

        /**
         * Gets an image from the cache.
         *
         * @param wavelength
         * @param diameter
         * @return Image, possibly null
         */
        public Image get( double wavelength, double diameter ) {
            Image image = null;
            Object diameterKey = diameterToKey( diameter );
            HashMap wavelengthMap = (HashMap) _diameterMap.get( diameterKey );
            if ( wavelengthMap != null ) {
                Object wavelengthKey = wavelengthToKey( wavelength );
                image = (Image) wavelengthMap.get( wavelengthKey );
            }
            return image;
        }

        /*
        * Converts a diameter to a key.
        */
        private Object diameterToKey( double diameter ) {
            return new Double( diameter );
        }

        /*
        * Converts a wavelength to a key.
        * Visible wavelengths are mapped with integer precision.
        * All UV wavelengths map to the same key, ditto for IR.
        */
        private Object wavelengthToKey( double wavelength ) {
            Object key;
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
}