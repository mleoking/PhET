// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

public class SimSharingProject extends JavaSimulationProject {
    public SimSharingProject( File file ) throws IOException {
        super( file );
    }

    //Built from util, so has a different path to trunk
    public File getTrunkAbsolute() {
        return getProjectDir().getParentFile().getParentFile(); // ../../trunk/
    }
}