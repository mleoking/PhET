/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering;

import java.awt.Image;

import edu.colorado.phet.common.view.util.PhetProjectConfig;


public class RSResources {

    /* not intended for instantiation */
    private RSResources() {}
    
    private static final PhetProjectConfig CONFIG = PhetProjectConfig.forProject( "rutherford-scattering" );
    
    public static PhetProjectConfig getConfig() {
        return CONFIG;
    }
    
    public static String getString( String name ) {
        return CONFIG.getString( name );
    }
    
    public static int getInt( String name, int defaultValue ) {
        return CONFIG.getInt( name, defaultValue );
    }
    
    public static Image getImage( String name ) {
        return CONFIG.getImage( name );
    }

}
