package edu.colorado.phet.buildtools.projects;

import java.io.File;
import java.io.IOException;

/**
 * This project is used so the build-tools project can build and deploy itself.
 */
public class BuildToolsProject extends JavaProject {
    public BuildToolsProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        return getProjectDir().getParentFile();
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
        System.out.println( "No launch files (JNLP) for updater." );
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
