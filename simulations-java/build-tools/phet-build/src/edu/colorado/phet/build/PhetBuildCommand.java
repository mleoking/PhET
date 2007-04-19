/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

import edu.colorado.phet.build.proguard.ProguardCommand;
import edu.colorado.phet.build.patterns.Command;

/**
 * This command builds a PhET project, together with any dependencies.
 */
public class PhetBuildCommand implements Command {
    private final PhetProject project;
    private final AntTaskRunner antTaskRunner;
    private final boolean shrink;

    public PhetBuildCommand( PhetProject project, AntTaskRunner taskRunner, boolean shrink ) {
        this.project       = project;
        this.antTaskRunner = taskRunner;
        this.shrink        = shrink;
    }

    public void execute() throws Exception {
        compile( project.getAllSourceRoots(), project.getAllJarFiles(), project.getClassesDirectory() );
        
        jar();

        PhetProguardConfigBuilder builder = new PhetProguardConfigBuilder();

        builder.setPhetProject( project );
        builder.setShrink( shrink );

        new ProguardCommand( builder.build(), antTaskRunner ).execute();
    }

    private void compile( File[] src, File[] classpath, File dst ) {
        // TODO: dst isn't used???
        PhetBuildUtils.antEcho( antTaskRunner, "Compiling " + project.getName() + "." );
        Javac javac = new Javac();
        javac.setSource( "1.4" );
        javac.setSrcdir( new Path( antTaskRunner.getProject(), toString( src ) ) );
        javac.setDestdir( project.getClassesDirectory() );
        javac.setClasspath( new Path( antTaskRunner.getProject(), toString( classpath ) ) );

        antTaskRunner.runTask( javac );

        PhetBuildUtils.antEcho( antTaskRunner, "Finished compiling " + project.getName() + "." );
    }

    private void jar() throws ManifestException {
        Jar jar = new Jar();
        File[] dataDirectories = project.getAllDataDirectories();
        for( int i = 0; i < dataDirectories.length; i++ ) {
            FileSet set = new FileSet();
            set.setDir( dataDirectories[i] );
            jar.addFileset( set );
        }
        jar.setBasedir( project.getClassesDirectory() );
        jar.setJarfile( project.getJarFile() );
        Manifest manifest = new Manifest();

        Manifest.Attribute attribute = new Manifest.Attribute();
        attribute.setName( "Main-Class" );
        attribute.setValue( project.getMainClass() );
        manifest.addConfiguredAttribute( attribute );
        jar.addConfiguredManifest( manifest );

        antTaskRunner.runTask( jar );
    }

    private String toString( File[] files ) {
        String string = "";
        for( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            string += file.getAbsolutePath();
            if( i < files.length - 1 ) {
                string += " : ";
            }
        }
        return string;
    }
}
