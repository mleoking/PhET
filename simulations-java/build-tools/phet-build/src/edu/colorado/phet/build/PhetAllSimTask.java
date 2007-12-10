package edu.colorado.phet.build;

import java.io.File;

/**
 * Author: Sam Reid
 * May 14, 2007, 5:47:07 PM
 */
public class PhetAllSimTask extends AbstractPhetTask {


    public File getBaseDir() {
        return getProject().getBaseDir();
    }
}
