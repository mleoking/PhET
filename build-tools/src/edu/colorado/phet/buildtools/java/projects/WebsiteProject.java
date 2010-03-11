package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.java.JavaProject;

/**
 * Project meant to compile the Wicket website
 */
public class WebsiteProject extends JavaProject {
    public WebsiteProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        return new File( getProjectDir(), "../.." );
    }

    public String getAlternateMainClass() {
        return null;
    }

    public String getProdServerDeployPath() {
        return null;
        //return BuildToolsPaths.BUILD_TOOLS_PROD_SERVER_DEPLOY_PATH;
    }

    public boolean isTestable() {
        return true;
    }

}