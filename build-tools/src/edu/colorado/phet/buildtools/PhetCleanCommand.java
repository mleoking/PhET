/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools;

import java.io.File;

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

        File header = project.getDeployHeaderFile();
        boolean del = header.delete();
        System.out.println( "Delete header file=" + del );

        File[] jars = project.getDeployDir().listFiles();
        for ( int i = 0; i < jars.length; i++ ) {
            File jar = jars[i];
            boolean deleted = jar.delete();
            System.out.println( "Delete " + jar + " = " + deleted );
        }

        Delete d = new Delete();
        d.setDir( project.getContribLicenseDir() );
        antTaskRunner.runTask( d );
    }
}
