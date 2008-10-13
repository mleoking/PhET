package edu.colorado.phet.dischargelamps;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Created by: Sam
 * Sep 8, 2008 at 8:06:12 AM
 */
public class DischargeLampsResources {

    private static final PhetResources RESOURCES = new PhetResources( DischargeLampsConfig.PROJECT_NAME );

    /* not intended for instantiation */
    private DischargeLampsResources() {}

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
