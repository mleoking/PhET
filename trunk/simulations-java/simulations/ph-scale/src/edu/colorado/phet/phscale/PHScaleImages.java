/* Copyright 2008, University of Colorado */

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
    public static final BufferedImage H2O_SMALL = PHScaleResources.getImage( "H2O-small.png" );
    public static final BufferedImage H3O_SMALL = PHScaleResources.getImage( "H3O-small.png" );
    public static final BufferedImage OH_SMALL = PHScaleResources.getImage( "OH-small.png" );
    public static final BufferedImage H2O_BIG = PHScaleResources.getImage( "H2O-big.png" );
    public static final BufferedImage H3O_BIG = PHScaleResources.getImage( "H3O-big.png" );
    public static final BufferedImage OH_BIG = PHScaleResources.getImage( "OH-big.png" );
}
