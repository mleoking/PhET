// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.constants;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Wrapper around the PhET resource loader.
 * If we decide to use a different technique to load resources in the 
 * future, all changes will be encapsulated here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSResources {
    
    private static final PhetResources RESOURCES = new PhetResources( ABSConstants.PROJECT );
    
    /* not intended for instantiation */
    private ABSResources() {}
    
    public static final PhetResources getResourceLoader() {
        return RESOURCES;
    }
    
    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name  );
    }
    
    public static final BufferedImage getBufferedImage( String name ) {
        return RESOURCES.getImage( name );
    }
}
