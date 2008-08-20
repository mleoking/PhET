/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import edu.colorado.phet.build.patterns.Command;
import edu.colorado.phet.build.proguard.ProguardCommand;

/**
 * This command builds a PhET project, together with any dependencies.
 */
public class PhetBuildCommand implements Command {
    private final PhetProject project;
    private final AntTaskRunner antTaskRunner;
    private final boolean shrink;
    private final File outputJar;
    public static final String FLAVOR_LAUNCHER = "edu.colorado.phet.common.phetcommon.view.util.FlavorLauncher";

    public PhetBuildCommand( PhetProject project, AntTaskRunner taskRunner, boolean shrink, File outputJar ) {
        this.project = project;
        this.antTaskRunner = taskRunner;
        this.shrink = shrink;
        this.outputJar = outputJar;
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

        PhetBuildUtils.antEcho( antTaskRunner, "Compiling " + project.getName() + ".", getClass() );

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
//        javac.setDebugLevel( "lines" );
        //"lines,source" appears to be necessary to get line number debug info
        javac.setDebugLevel( "lines,source" );
        javac.setDebug( true );

        antTaskRunner.runTask( javac );

        PhetBuildUtils.antEcho( antTaskRunner, "Finished compiling " + project.getName() + ".", getClass() );
    }

    private void jar() throws ManifestException {
        Jar jar = new Jar();
        File[] dataDirectories = project.getAllDataDirectories();
        for ( int i = 0; i < dataDirectories.length; i++ ) {
            FileSet set = new FileSet();
            set.setDir( dataDirectories[i] );
            jar.addFileset( set );
        }
        jar.setBasedir( project.getClassesDirectory() );
        jar.setDestFile( project.getJarFile() );
        Manifest manifest = new Manifest();

        Manifest.Attribute attribute = new Manifest.Attribute();

        //need to use flavor launcher if there are multiple flavors or if any flavor contains args
        //for now, let's just use FlavorLauncher for all usages.

        attribute.setName( "Main-Class" );
        //todo: support a main-class chooser & launcher
        attribute.setValue( FLAVOR_LAUNCHER );

        File flavorsProp = createProjectPropertiesFile();

        FileSet flavorFileSet = new FileSet();
        flavorFileSet.setFile( flavorsProp );
        jar.addFileset( flavorFileSet );

        manifest.addConfiguredAttribute( attribute );
        jar.addConfiguredManifest( manifest );

        antTaskRunner.runTask( jar );
    }

    /*
     * Creates a properties file that describes things about the project.
     */
    private File createProjectPropertiesFile() {
        
        // create the various properties
        Properties properties = new Properties();
        properties.setProperty( "project.name", project.getName() );
        for ( int i = 0; i < project.getFlavors().length; i++ ) {
            PhetProjectFlavor flavor = project.getFlavors()[i];
            properties.setProperty( "project.flavor." + flavor.getFlavorName() + ".mainclass", flavor.getMainclass() );
            properties.setProperty( "project.flavor." + flavor.getFlavorName() + ".title", flavor.getTitle() );
            String args = "";
            String[] a = flavor.getArgs();
            for ( int j = 0; j < a.length; j++ ) {
                args += a[j] + " ";
            }
            properties.setProperty( "project.flavor." + flavor.getFlavorName() + ".args", args.trim() );
        }
        
        // write the properties to a file
        File file = new File( project.getAntOutputDir(), "project.properties" );
        file.getParentFile().mkdirs();
        try {
            properties.store( new FileOutputStream( file ), null );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        
        // return the file
        return file;
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
        for ( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            string += file.getAbsolutePath();
            if ( i < files.length - 1 ) {
                string += " : ";
            }
        }
        return string;
    }
}
