package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.java.JavaProject;

/**
 * Used for building the flash-launcher .class files that will be used in PhetFlashProject.
 * This project is not intended to be used or deployed by itself.
 */
public class FlashLauncherProject extends JavaProject {
    public FlashLauncherProject( File trunk ) throws IOException {
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
}
