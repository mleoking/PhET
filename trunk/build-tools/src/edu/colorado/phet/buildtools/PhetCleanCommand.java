/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.FileFilter;

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

        File[] files = project.getDeployDir().listFiles(new FileFilter() {
            public boolean accept( File pathname ) {
                return !pathname.isDirectory();
            }
        } );
        for ( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            boolean deleted = file.delete();
            System.out.println( "Delete " + file + " = " + deleted );
        }

        Delete d = new Delete();
        d.setDir( project.getContribLicenseDir() );
        antTaskRunner.runTask( d );
    }
}
