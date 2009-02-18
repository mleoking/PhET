package edu.colorado.phet.common.phetcommon.updates;

/**
 * IManualUpdateChecker is the interface for manually requesting an update check.
 */
public interface IManualUpdateChecker {
    
    /**
     * Checks for sim updates and handles any user interaction that is involved.
     */
    public void checkForSimUpdates();
    
    /**
     * Checks for installer updates and handles any user interaction that is involved.
     */
    public void checkForInstallerUpdates();
}
