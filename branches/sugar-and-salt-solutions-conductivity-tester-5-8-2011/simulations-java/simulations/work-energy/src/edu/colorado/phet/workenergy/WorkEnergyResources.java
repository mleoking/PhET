// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * @author Sam Reid
 */
public class WorkEnergyResources {
    private static final PhetResources phetResources = new PhetResources( "work-energy" );

    public static BufferedImage getImage( String image ) {
        return phetResources.getImage( image );
    }
}
