package edu.colorado.phet.sound;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;


public class SoundResources {

    private static PhetResources phetResources = new PhetResources( SoundConfig.PROJECT_NAME );

    public static PhetResources getResourceLoader() {
        return phetResources;
    }

    public static String getString( String key ) {
        return getResourceLoader().getLocalizedString( key );
    }
}
