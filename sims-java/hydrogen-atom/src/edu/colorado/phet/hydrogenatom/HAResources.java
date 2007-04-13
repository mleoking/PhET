/* Copyright 2007, University of Colorado */

package edu.colorado.phet.hydrogenatom;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.PhetCommonProjectConfig;
import edu.colorado.phet.common.view.util.PhetProjectConfig;
import edu.umd.cs.piccolo.nodes.PImage;


public class HAResources {
    
    private static final PhetProjectConfig CONFIG = PhetProjectConfig.forProject( "hydrogen-atom" );
    
    /* not intended for instantiation */
    private HAResources() {}
    
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
    
    public static final PImage getImageNode( String name ) {
        return new PImage( CONFIG.getImage( name ) );
    }
    
    public static final String getCommonString( String name ) {
        return PhetCommonProjectConfig.getInstance().getString( name );
    }
    
    public static final BufferedImage getCommonImage( String name ) {
        return PhetCommonProjectConfig.getInstance().getImage( name );
    }
}
