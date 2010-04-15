/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

import java.awt.image.BufferedImage;

/**
 * Images used throughout the simulation.
 * Loaded statically to make it easy to debug missing images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLImages {

    /* not intended for instantiation */
    private CLImages() {}
    
    private static final BufferedImage getBufferedImage( String resourceName ) {
        return CLResources.getBufferedImage( resourceName );
    }
}
