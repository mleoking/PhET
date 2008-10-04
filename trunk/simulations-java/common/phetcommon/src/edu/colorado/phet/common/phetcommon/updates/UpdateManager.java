package edu.colorado.phet.common.phetcommon.updates;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public class UpdateManager {
    private String project;
    private PhetVersion currentVersion;
    private IVersionChecker versionChecker = new DefaultVersionChecker();

    public UpdateManager( String project, PhetVersion currentVersion ) {
        this.project = project;
        this.currentVersion = currentVersion;
    }

    public boolean isUpdateAvailable() throws IOException {
        PhetVersion remoteVersion = versionChecker.getVersion( project );
        return remoteVersion.isGreaterThan(currentVersion);
    }

}
