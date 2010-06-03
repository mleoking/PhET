/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.constants;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Wrapper around the PhET resource loader.
 * If we decide to use a different technique to load resources in the 
 * future, all changes will be encapsulated here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSResources {
    
    private static final PhetResources RESOURCES = new PhetResources( ABSConstants.PROJECT_NAME );
    
    /* not intended for instantiation */
    private ABSResources() {}
    
    public static final PhetResources getResourceLoader() {
        return RESOURCES;
    }
    
    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name  );
    }
    
    public static final char getChar( String name, char defaultValue ) {
        return RESOURCES.getLocalizedChar( name, defaultValue );
    }

    public static final int getInt( String name, int defaultValue ) {
        return RESOURCES.getLocalizedInt( name, defaultValue );
    }
    
    public static final BufferedImage getBufferedImage( String name ) {
        return RESOURCES.getImage( name );
    }
    
    public static final PImage getImageNode( String name ) {
        return new PImage( RESOURCES.getImage( name ) );
    }
    
    public static final String getCommonString( String name ) {
        return PhetCommonResources.getInstance().getLocalizedString( name );
    }
    
    public static final BufferedImage getCommonImage( String name ) {
        return PhetCommonResources.getInstance().getImage( name );
    }
}
