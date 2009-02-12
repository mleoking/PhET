package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.IOException;

public class UpdaterProject extends PhetJavaProject {
    public UpdaterProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunk() {
        return getProjectDir().getParentFile()//util
                .getParentFile();// trunk
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
        System.out.println( "No launch files (JNLP) for updater." );
    }

    public String getAlternateMainClass() {
        return "edu.colorado.phet.updater.UpdaterBootstrap";
    }
}
