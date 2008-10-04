package edu.colorado.phet.common.phetcommon.updates;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public interface IVersionChecker {
    PhetVersion getVersion(String project) throws IOException;
}
