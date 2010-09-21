/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * GlaciersResources is a wrapper around the PhET resource loader.
 * If we decide to use a different technique to load resources in the 
 * future, all changes will be encapsulated here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersResources {
    
    private static final PhetResources RESOURCES = new PhetResources( GlaciersConstants.PROJECT_NAME );
    
    /* not intended for instantiation */
    private GlaciersResources() {}
    
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
    
    public static final BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }
    
    public static final Icon getIcon( String name ) {
        return new ImageIcon( getImage( name ) );
    }
    
    public static final InputStream getResourceAsStream( String name ) {
        InputStream s = null;
        try {
            s = RESOURCES.getResourceAsStream( name );
        }
        catch ( IOException e ) {
            System.err.println( "failed to get InputStream for resource named " + name );
        }
        return s;
    }
    
    public static final String getCommonString( String name ) {
        return PhetCommonResources.getInstance().getLocalizedString( name );
    }
    
    public static final BufferedImage getCommonImage( String name ) {
        return PhetCommonResources.getInstance().getImage( name );
    }
}
