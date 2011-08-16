// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Images, statically loaded to identify missing images at startup.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPImages {

    private MPImages() {
    }

    private static final PhetResources RESOURCES = new PhetResources( MPConstants.PROJECT_NAME );

    public static final Image ROTATION_CURSOR = RESOURCES.getImage( "rotationCursor.png" );
}
