/* Copyright 2007, University of Colorado */

package edu.colorado.phet.quantumtunneling;

import java.awt.Image;

import edu.colorado.phet.common.view.util.PhetProjectConfig;


public class QTResources {
    
    /* not intended for instantiation */
    private QTResources() {}
    
    private static final PhetProjectConfig CONFIG = PhetProjectConfig.forProject( "quantum-tunneling" );
    
    // Images
    public static final Image IMAGE_ARROW_L2R = CONFIG.getImage( "arrowL2R.png" );
    public static final Image IMAGE_ARROW_L2R_SELECTED = CONFIG.getImage( "arrowL2RSelected.png" );
    public static final Image IMAGE_ARROW_R2L = CONFIG.getImage( "arrowR2L.png" );
    public static final Image IMAGE_ARROW_R2L_SELECTED = CONFIG.getImage( "arrowR2LSelected.png" );
    public static final Image IMAGE_CLOCK = CONFIG.getImage( "clock.png" );
    public static final Image IMAGE_ZOOM_IN = CONFIG.getImage( "zoomIn.gif" );
    public static final Image IMAGE_ZOOM_OUT = CONFIG.getImage( "zoomOut.gif" );

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

}
