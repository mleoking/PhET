package edu.colorado.phet.common.phetcommon.updates;


/**
 * IVersionSkipper is the interface for skipping a version of something.
 */
public interface IVersionSkipper {
    
    /**
     * Indicates that a specified version should be skipped.
     * No further automatic updates will be provided for this version.
     * @param skipVersion
     */
    public void setSkippedVersion( int skipVersion );
    
    /**
     * Gets the version that we're skipping.
     * Return 0 if no version is being skipped.
     * @return
     */
    public int getSkippedVersion();
    
    /**
     * Should a specified version be skipped?
     * @param version
     * @return
     */
    public boolean isSkipped( int version );
}
