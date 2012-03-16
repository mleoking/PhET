/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public interface AntTaskRunner {
    void runTask( Task child );

    Project getProject();
}
