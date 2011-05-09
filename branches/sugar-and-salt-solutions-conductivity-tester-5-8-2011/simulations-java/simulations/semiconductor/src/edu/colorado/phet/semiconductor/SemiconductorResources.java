// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class SemiconductorResources {
    private static PhetResources resources = new PhetResources( "semiconductor" );

    public static String getString( String key ) {
        return resources.getLocalizedString( key );
    }

    public static PhetResources getResourceLoader() {
        return resources;
    }
}
