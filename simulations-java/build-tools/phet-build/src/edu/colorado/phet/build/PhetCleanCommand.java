/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import org.apache.tools.ant.taskdefs.Delete;

public class PhetCleanCommand {
    private final PhetProject project;
    private final AntTaskRunner antTaskRunner;

    public PhetCleanCommand( PhetProject project, AntTaskRunner antTaskRunner ) {
        this.project = project;
        this.antTaskRunner = antTaskRunner;
    }

    public void execute() throws Exception {
        Delete t = new Delete();

        t.setDir( project.getAntOutputDir() );

        antTaskRunner.runTask( t );
    }
}
