package edu.colorado.phet.common.phetcommon.application;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public interface ISimInfo {
    String getTitle();
    String getDescription();
    PhetVersion getVersion();
    String getCredits();
}
