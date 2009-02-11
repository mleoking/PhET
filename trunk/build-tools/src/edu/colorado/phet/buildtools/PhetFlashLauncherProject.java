package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 11, 2009
 * Time: 3:30:15 PM
 */
public class PhetFlashLauncherProject extends PhetJavaProject{
    public PhetFlashLauncherProject( File trunk ) throws IOException {
        super(new File( trunk,"simulations-flash/flash-launcher"));
    }

    public File getTrunk() {
        return new File(getProjectDir(),"../../");
    }
}
