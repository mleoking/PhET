/* Copyright 2007, University of Colorado */

package edu.colorado.phet.boundstates;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.view.util.PhetProjectConfig;
import edu.umd.cs.piccolo.nodes.PImage;


public class BSResources {
    
    private static PhetProjectConfig _config;
    
    /* not intended for instantiation */
    private BSResources() {}
    
    public static final void setConfig( PhetProjectConfig config ) {
        _config = config;
    }
    
    public static final PhetProjectConfig getConfig() {
        return _config;
    }
    
    public static final String getString( String name ) {
        return _config.getString( name  );
    }
    
    public static final char getChar( String name, char defaultValue ) {
        return _config.getChar( name, defaultValue );
    }

    public static final int getInt( String name, int defaultValue ) {
        return _config.getInt( name, defaultValue );
    }
    
    public static final String getVersion() {
        return _config.getVersion().toString();
    }
    
    public static final BufferedImage getImage( String name ) {
        return _config.getImage( name );
    }
    
    public static final PImage getImageNode( String name ) {
        return new PImage( _config.getImage( name ) );
    }
}
