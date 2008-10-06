package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public interface IVersionSkipper {
    void skipThisVersion( String projectName, String flavor, PhetVersion newVersion );
}
