// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Created by: Sam
 * Jun 17, 2008 at 1:02:59 PM
 */
public class Force1DResources {
    private static final PhetResources RESOURCES = new PhetResources( "forces-1d" );

    public static final PhetResources getResourceLoader() {
        return RESOURCES;
    }

    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name );
    }

    public static String get( String s ) {
        return getString( s );
    }

    public static void setStrings( String s ) {
    }

    public static void getInstance() {
    }

    public static String getCommonString( String s ) {
        return PhetCommonResources.getInstance().getLocalizedString( s );
    }
}
