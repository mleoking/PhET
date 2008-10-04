package edu.colorado.phet.common.phetcommon.updates;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetVersionInfo;

public interface IVersionChecker {
    PhetVersionInfo getVersion(String project) throws IOException;
}
