package edu.colorado.phet.motion2d;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Sep 23, 2008
 * Time: 11:47:41 AM
 */
public class Motion2DResources {
    private static PhetResources phetResources = new PhetResources( "motion-2d" );

    public static PhetResources getResourceLoader() {
        return phetResources;
    }

    public static String getString( String key ) {
        return getResourceLoader().getLocalizedString( key );
    }
}
