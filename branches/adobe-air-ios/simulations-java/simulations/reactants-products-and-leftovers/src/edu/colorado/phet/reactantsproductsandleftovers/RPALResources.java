// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A wrapper around the PhET resource loader.
 * If we decide to use a different technique to load resources in the 
 * future, all changes will be encapsulated here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALResources {
    
    private static final PhetResources RESOURCES = new PhetResources( RPALConstants.PROJECT_NAME );
    
    /* not intended for instantiation */
    private RPALResources() {}

    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name  );
    }
    
    public static final BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }
}
