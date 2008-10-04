package edu.colorado.phet.common.phetcommon.updates;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetVersionInfo;

public class UpdateManager {
    private String project;
    private PhetVersionInfo currentVersion;
    private IVersionChecker versionChecker = new DefaultVersionChecker();

    public UpdateManager( String project, PhetVersionInfo currentVersion ) {
        this.project = project;
        this.currentVersion = currentVersion;
    }

    public boolean isUpdateAvailable() throws IOException {
        PhetVersionInfo remoteVersion = versionChecker.getVersion( project );
        return remoteVersion.isGreaterThan(currentVersion);
    }

}
