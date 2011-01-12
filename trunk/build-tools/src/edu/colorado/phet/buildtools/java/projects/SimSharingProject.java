// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.java.JavaProject;

/**
 * build the Translation Utility (translation-utility.jar)
 */
public class SimSharingProject extends JavaProject {
    public SimSharingProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        return getProjectDir().getParentFile().getParentFile(); // ../../trunk/
    }

    public String getAlternateMainClass() {
        return null;
    }

    public File getDefaultDeployJar() {
        return new File( getDeployDir(), "sim-sharing.jar" );
    }

    public String getProdServerDeployPath() {
        return BuildToolsPaths.SIM_SHARING_PROD_SERVER_DEPLOY_PATH;
    }

    public boolean isTestable() {
        return true;
    }
}
