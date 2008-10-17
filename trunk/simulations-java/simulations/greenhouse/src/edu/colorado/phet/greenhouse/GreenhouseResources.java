package edu.colorado.phet.greenhouse;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;


public class GreenhouseResources {

    private static final PhetResources RESOURCES = new PhetResources( GreenhouseConfig.PROJECT_NAME );

    /* not intended for instantiation */
    private GreenhouseResources() {}

    public static final PhetResources getResourceLoader() {
        return RESOURCES;
    }

    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name );
    }

    public static final BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }

    public static final String getCommonString( String name ) {
        return PhetCommonResources.getInstance().getLocalizedString( name );
    }

    public static final BufferedImage getCommonImage( String name ) {
        return PhetCommonResources.getInstance().getImage( name );
    }
}
