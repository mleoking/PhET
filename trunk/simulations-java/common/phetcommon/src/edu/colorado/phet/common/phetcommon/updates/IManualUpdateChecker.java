package edu.colorado.phet.common.phetcommon.updates;

/**
 * IManualUpdateChecker is the interface for manually requesting an update check.
 */
public interface IManualUpdateChecker {
    
    /**
     * Checks for updates and handles any user interaction that is involved.
     */
    void checkForUpdates();
}
