// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.photonabsorption;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * @author Sam Reid
 */
public class PhotonAbsorptionResources {
    private static final PhetResources RESOURCES = new PhetResources( "photon-absorption" );

    public static BufferedImage getImage( String imageName ) {
        return RESOURCES.getImage( imageName );
    }
}
