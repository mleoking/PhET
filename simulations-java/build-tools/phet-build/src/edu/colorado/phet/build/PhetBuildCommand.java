/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import edu.colorado.phet.build.patterns.Command;
import edu.colorado.phet.build.proguard.ProguardCommand;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import java.io.File;

/**
 * This command builds a PhET project, together with any dependencies.
 */
public class PhetBuildCommand implements Command {
    private final PhetProject project;
    private final AntTaskRunner antTaskRunner;
    private final boolean shrink;
    private final File outputJar;

    public PhetBuildCommand( PhetProject project, AntTaskRunner taskRunner, boolean shrink, File outputJar ) {
        this.project       = project;
        this.antTaskRunner = taskRunner;
        this.shrink        = shrink;
        this.outputJar     = outputJar;
    }

    public void execute() throws Exception {
        clean();
        compile();        
        jar();
        proguard();
    }

    private void clean() throws Exception {
        new PhetCleanCommand( project, antTaskRunner ).execute();
    }

    private void compile() {
        File[] src = project.getAllSourceRoots();
        File[] classpath = project.getAllJarFiles();
        
        PhetBuildUtils.antEcho( antTaskRunner, "Compiling " + project.getName() + "." );
        
        Javac javac = new Javac();
        javac.setSource( "1.4" );
        javac.setSrcdir( new Path( antTaskRunner.getProject(), toString( src ) ) );
        javac.setDestdir( project.getClassesDirectory() );
        javac.setClasspath( new Path( antTaskRunner.getProject(), toString( classpath ) ) );
        //http://nileshbansal.blogspot.com/2006/08/java-exception-unknown-source.html
//                        debug="on" debuglevel="lines,vars,source"
        //results for Energy Skate Park output 4-24-2007
//          no debug: 1.02 MB
//          lines, vars, source: 1.15 MB
//          lines: 1.07 MB
//        javac.setDebugLevel( "lines,vars,source" );
        javac.setDebugLevel( "lines" );
        javac.setDebug( true );

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
        jar.setDestFile( project.getJarFile() );
        Manifest manifest = new Manifest();

        Manifest.Attribute attribute = new Manifest.Attribute();
        attribute.setName( "Main-Class" );
        attribute.setValue( project.getMainClass() );
        manifest.addConfiguredAttribute( attribute );
        jar.addConfiguredManifest( manifest );

        antTaskRunner.runTask( jar );
    }

    private void proguard() throws Exception {
        PhetProguardConfigBuilder builder = new PhetProguardConfigBuilder();

        builder.setDestJar( outputJar );
        builder.setPhetProject( project );
        builder.setShrink( shrink );

        new ProguardCommand( builder.build(), antTaskRunner ).execute();
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
