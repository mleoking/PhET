/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.FileSet;

import java.io.File;

import edu.colorado.phet.build.proguard.ProguardCommand;


public class PhetProjectManager {
    private final PhetProject project;
    private final AntTaskRunner antTaskRunner;

    public PhetProjectManager( PhetProject project, AntTaskRunner taskRunner ) {
        this.project = project;
        this.antTaskRunner = taskRunner;
    }

    private void compile( File[] src, File[] classpath, File dst ) {
        // TODO: dst isn't used???
        output( "compiling " + project.getName() );
        Javac javac = new Javac();
        javac.setSource( "1.4" );
        javac.setSrcdir( new Path( antTaskRunner.getProject(), toString( src ) ) );
        javac.setDestdir( project.getClassesDirectory() );
        javac.setClasspath( new Path( antTaskRunner.getProject(), toString( classpath ) ) );
        antTaskRunner.runTask( javac );
        output( "Finished compiling " + project.getName() + "." );
    }

    private void jar() {
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
        try {
            Manifest.Attribute attribute = new Manifest.Attribute();
            attribute.setName( "Main-Class" );
            attribute.setValue( project.getMainClass() );
            manifest.addConfiguredAttribute( attribute );
            jar.addConfiguredManifest( manifest );
        }
        catch( ManifestException e ) {
            e.printStackTrace();
        }
        
        antTaskRunner.runTask( jar );
    }

    public void build( boolean shrink ) {
        compile( project.getAllSourceRoots(), project.getAllJarFiles(), project.getClassesDirectory() );
        jar();

        PhetProguardConfigBuilder builder = new PhetProguardConfigBuilder();

        builder.setPhetProject( project );
        builder.setShrink( shrink );

        try {
            new ProguardCommand( builder.build(), antTaskRunner ).execute();
        }
        catch( Exception e ) {
            // TODO
            e.printStackTrace();
        }
    }


    private void output( String s ) {
        Echo echo = new Echo();
        echo.setMessage( s );
        antTaskRunner.runTask( echo );
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
