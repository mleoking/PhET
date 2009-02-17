package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.java.JavaProject;

/**
 * Builds the build-tools project itself
 */
public class BuildToolsProject extends JavaProject {
    public BuildToolsProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        return getProjectDir().getParentFile();
    }

    public String getAlternateMainClass() {
        return null;
    }

    public String getProdServerDeployPath() {
        return null;
    }

    //TODO: should be written to bin/ or other useful location
    public String getDevDirectoryBasename() {
        return "current-version";
    }

}
