/* Copyright 2007, University of Colorado */

package edu.colorado.phet.quantumtunneling;

import java.awt.Image;

import edu.colorado.phet.common.PhetCommonProjectConfig;
import edu.colorado.phet.common.view.util.PhetProjectConfig;


public class QTResources {
    
    /* not intended for instantiation */
    private QTResources() {}
    
    private static final PhetProjectConfig CONFIG = PhetProjectConfig.forProject( "quantum-tunneling" );

    public static PhetProjectConfig getConfig() {
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
    
    public static final Image getImage( String name ) {
        return CONFIG.getImage( name );
    }
    
    public static final String getCommonString( String name ) {
        return PhetCommonProjectConfig.getInstance().getString( name  );
    }
    
    public static final Image getCommonImage( String name ) {
        return PhetCommonProjectConfig.getInstance().getImage( name );
    }
}
