// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.testlwjglproject;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This is a convenience wrapper around the PhET resource loader.
 */
public class TestProjectResources {

    private static final PhetResources RESOURCES = new PhetResources( TestProjectConstants.PROJECT_NAME );

    /* not intended for instantiation */
    private TestProjectResources() {
    }

    public static final PhetResources getResourceLoader() {
        return RESOURCES;
    }

    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name );
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
