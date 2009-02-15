package edu.colorado.phet.buildtools.java;

import java.io.File;
import java.io.IOException;

/**
 * Used for building the .class files that will be used in PhetFlashProject.
 * This project is not intended to be used or deployed by itself.
 */
public class PhetFlashLauncherProject extends JavaProject {
    public PhetFlashLauncherProject( File trunk ) throws IOException {
        super( new File( trunk, "simulations-flash/flash-launcher" ) );
    }

    public File getTrunkAbsolute() {
        return new File( getProjectDir(), "../../" );
    }

    public String getAlternateMainClass() {
        return null;
    }

    public String getProdServerDeployPath() {
        return null;
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
    }
}
