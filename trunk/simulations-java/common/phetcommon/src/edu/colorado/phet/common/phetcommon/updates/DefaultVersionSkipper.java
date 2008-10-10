package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public class DefaultVersionSkipper implements IVersionSkipper {
    public void skipThisVersion( String projectName, String flavor, PhetVersion newVersion ) {
        PhetPreferences.getInstance().setSkipUpdate( projectName, flavor, newVersion );
    }
}
