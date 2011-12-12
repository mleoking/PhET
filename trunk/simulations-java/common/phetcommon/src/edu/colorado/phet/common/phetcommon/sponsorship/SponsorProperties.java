// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.sponsorship;

import java.util.Properties;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

/**
 * Encapsulation of the properties related to the sponsor feature.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SponsorProperties {

    private static final String PROPERTIES_FILE_NAME = "sponsor.properties";

    private final String sim;
    private final Properties properties;

    public SponsorProperties( PhetApplicationConfig config ) {

        // project may contain more than one sim (aka flavor)
        sim = config.getFlavor();

        // properties are loaded from a properties file
        properties = config.getResourceLoader().getProperties( PROPERTIES_FILE_NAME );
    }

    public boolean isWellFormed() {
        return getImageResourceName() != null; // image is the only required property
    }

    public String getImageResourceName() {
        return properties.getProperty( sim + ".image" );
    }

    public String getURL() {
        return properties.getProperty( sim + ".url" );
    }

    public String getSinceDate() {
        return properties.getProperty( sim + ".sinceDate" );
    }
}
