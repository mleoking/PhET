/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;


public class RSResources {

    /* not intended for instantiation */
    private RSResources() {}
    
    private static final PhetResources RESOURCES = new PhetResources( RSConstants.PROJECT_NAME );
    
    public static PhetResources getResourceLoader() {
        return RESOURCES;
    }
    
    public static String getString( String name ) {
        return RESOURCES.getLocalizedString( name );
    }
    
    public static int getInt( String name, int defaultValue ) {
        return RESOURCES.getLocalizedInt( name, defaultValue );
    }
    
    public static Image getImage( String name ) {
        return RESOURCES.getImage( name );
    }

}
