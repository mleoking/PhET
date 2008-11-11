package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

/**
 * DefaultVersionSkipper skips a version by recording the 
 * skipped version number in the user's preferences.
 */
public class DefaultVersionSkipper implements IVersionSkipper {
    public void setSkippedVersion( String projectName, String flavor, PhetVersion skipVersion ) {
        PhetPreferences.getInstance().setSkipUpdate( projectName, flavor, skipVersion );
    }
}
