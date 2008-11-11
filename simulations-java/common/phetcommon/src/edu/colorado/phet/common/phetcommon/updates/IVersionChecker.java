package edu.colorado.phet.common.phetcommon.updates;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

/**
 * IVersionChecker is the interface for getting the most up-to-date version information.
 */
public interface IVersionChecker {
    
    /**
     * Gets the most up-to-date version number of a project.
     * 
     * @param project
     * @return
     * @throws IOException
     */
    PhetVersion getVersion(String project) throws IOException;
}
