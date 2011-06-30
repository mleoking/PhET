//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A wrapper around the PhET resource loader.
 * If we decide to use a different technique to load resources in the
 * future, all changes will be encapsulated here.
 */
public class BuildAMoleculeResources {

    private static final PhetResources RESOURCES = new PhetResources( BuildAMoleculeConstants.PROJECT_NAME );

    /* not intended for instantiation */
    private BuildAMoleculeResources() {
    }

    public static PhetResources getResourceLoader() {
        return RESOURCES;
    }

    public static String getString( String name ) {
        return RESOURCES.getLocalizedString( name );
    }

    public static char getChar( String name, char defaultValue ) {
        return RESOURCES.getLocalizedChar( name, defaultValue );
    }

    public static int getInt( String name, int defaultValue ) {
        return RESOURCES.getLocalizedInt( name, defaultValue );
    }

    public static BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }

    public static PImage getImageNode( String name ) {
        return new PImage( RESOURCES.getImage( name ) );
    }

    public static String getCommonString( String name ) {
        return PhetCommonResources.getInstance().getLocalizedString( name );
    }

    public static BufferedImage getCommonImage( String name ) {
        return PhetCommonResources.getInstance().getImage( name );
    }
}
