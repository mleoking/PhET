package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.java.JavaProject;

/**
 * builds the PhET Build GUI (phet-build-gui.jar)
 */
public class PhetBuildGUIProject extends JavaProject {
    public PhetBuildGUIProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        return getProjectDir().getParentFile();
    }

    public String getAlternateMainClass() {
        return "edu.colorado.phet.buildtools.gui.PhetBuildGUI";
    }

    public String getProdServerDeployPath() {
        return null;
    }

    public File getDefaultDeployJar() {
        return new File( getDeployDir(), "phet-build-gui.jar" );
    }

}
