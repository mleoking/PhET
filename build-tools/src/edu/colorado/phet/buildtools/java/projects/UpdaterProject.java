package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.java.JavaProject;

public class UpdaterProject extends JavaProject {
    public UpdaterProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        return getProjectDir().getParentFile()//util
                .getParentFile();// trunk
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
        System.out.println( "No launch files (JNLP) for updater." );
    }

    public String getAlternateMainClass() {
        return "edu.colorado.phet.phetupdater.UpdaterBootstrap";
    }

    public File getDefaultDeployJar() {
        return new File( getDeployDir(), "phet-updater.jar" );
    }

    public String getProdServerDeployPath() {
        return "/web/htdocs/phet/phet-dist/updater";
    }
}
