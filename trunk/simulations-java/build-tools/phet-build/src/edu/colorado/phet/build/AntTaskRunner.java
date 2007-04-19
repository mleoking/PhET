/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;

public interface AntTaskRunner {
    void runTask( Task child );

    Project getProject();
}
