package edu.colorado.phet.phscale;

import java.awt.image.BufferedImage;

/**
 * This is a collection of images used by this simulation.
 * All images are loaded statically so that we can easily test for missing images on start up.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleImages {

    /* not intended for instantiation */
    private PHScaleImages() {}
    
    public static final BufferedImage FAUCET = PHScaleResources.getImage( "faucet.png" );
    
}
