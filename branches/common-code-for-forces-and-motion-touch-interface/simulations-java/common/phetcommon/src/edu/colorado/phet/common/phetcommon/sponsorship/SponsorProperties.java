// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.sponsorship;

import java.util.Properties;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

/**
 * Encapsulation of the properties related to the sponsor feature.
 * Properties that are not specified are null.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SponsorProperties {

    private final String sim;
    private final Properties properties;

    public SponsorProperties( PhetApplicationConfig config ) {

        // project may contain more than one sim (aka flavor)
        sim = config.getFlavor();

        // properties are loaded from a properties file, bundled as a Jar resource
        properties = config.getResourceLoader().getProperties( "sponsor.properties" );
    }

    // Well-formed if all required properties are specified.
    public boolean isWellFormed() {
        return getImageResourceName() != null;
    }

    // Name of the image resource provided by the sponsor.
    public String getImageResourceName() {
        return properties.getProperty( sim + ".image" );
    }

    // Actual URL that we follow when clicking the hyperlink.
    public String getActualURL() {
        return properties.getProperty( sim + ".actualURL" );
    }

    // URL displayed to the user.
    public String getVisibleURL() {
        return properties.getProperty( sim + ".visibleURL" );
    }

    // The year when the sponsor started its sponsorship.
    public String getSinceYear() {
        return properties.getProperty( sim + ".sinceYear" );
    }
}
