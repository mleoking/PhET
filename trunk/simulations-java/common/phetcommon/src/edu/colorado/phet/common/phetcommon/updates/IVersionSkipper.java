package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

/**
 * IVersionSkipper is the interface for skipping a version of a sim.
 * If you skip a version, automatic update notification will not occur
 * until another version becomes available. 
 */
public interface IVersionSkipper {
    
    /**
     * Indicates that a specified version of a sim should be skipped.
     * No further automatic updates will be provided for this version.
     * @param projectName
     * @param flavor
     * @param skipVersion
     */
    void setSkippedVersion( String projectName, String flavor, PhetVersion skipVersion );
}
