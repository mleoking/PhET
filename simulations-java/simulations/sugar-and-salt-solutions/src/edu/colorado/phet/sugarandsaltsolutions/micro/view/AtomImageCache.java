// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.nodes.ShadedSphereNode;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.toBufferedImage;

/**
 * Caches images for atoms because creating a new one for each particle creates a 1 second delay when creating sucrose crystals.
 *
 * @author Sam Reid
 */
public class AtomImageCache {
    //Keys for caching the images by diameter and color
    static class Key {
        public final double diameter;
        public final Color color;

        Key( double diameter, Color color ) {
            this.diameter = diameter;
            this.color = color;
        }

        //Automatically generated (by IDEA)
        @Override public boolean equals( Object o ) {
            if ( this == o ) { return true; }
            if ( o == null || getClass() != o.getClass() ) { return false; }

            Key key = (Key) o;

            return Double.compare( key.diameter, diameter ) == 0 && color.equals( key.color );
        }

        //Automatically generated (by IDEA)
        @Override public int hashCode() {
            int result;
            long temp;
            temp = diameter != +0.0d ? Double.doubleToLongBits( diameter ) : 0L;
            result = (int) ( temp ^ ( temp >>> 32 ) );
            result = 31 * result + color.hashCode();
            return result;
        }
    }

    //Map that contains the created images
    public static final HashMap<Key, BufferedImage> map = new HashMap<Key, BufferedImage>();

    //Load a cached image, or create and cache one if it doesn't already exist in the cache
    public static BufferedImage getAtomImage( double diameter, Color color ) {
        Key key = new Key( diameter, color );
        if ( !map.containsKey( key ) ) {
            map.put( key, toBufferedImage( new ShadedSphereNode( diameter, color, Color.white, Color.black ).toImage() ) );
        }
        return map.get( key );
    }
}