/* Copyright 2008, University of Colorado */

package edu.colorado.phet.radiowaves;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;


public class RadioWavesResources {

    private static PhetResources phetResources = new PhetResources( RadioWavesConstants.PROJECT_NAME );

    public static PhetResources getResourceLoader() {
        return phetResources;
    }

    public static String getString( String key ) {
        return getResourceLoader().getLocalizedString( key );
    }
}
