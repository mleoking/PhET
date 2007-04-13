/* Copyright 2007, University of Colorado */

package edu.colorado.phet.fourier;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.view.util.PhetProjectConfig;


public class FourierResources {
    
    private static final PhetProjectConfig CONFIG = PhetProjectConfig.forProject( "fourier" );
    
    /* not intended for instantiation */
    private FourierResources() {}
    
    public static final PhetProjectConfig getConfig() {
        return CONFIG;
    }
    
    public static final String getString( String name ) {
        return CONFIG.getString( name  );
    }
    
    public static final char getChar( String name, char defaultValue ) {
        return CONFIG.getChar( name, defaultValue );
    }

    public static final int getInt( String name, int defaultValue ) {
        return CONFIG.getInt( name, defaultValue );
    }
    
    public static final String getVersion() {
        return CONFIG.getVersion().toString();
    }
    
    public static final BufferedImage getImage( String name ) {
        return CONFIG.getImage( name );
    }
}
