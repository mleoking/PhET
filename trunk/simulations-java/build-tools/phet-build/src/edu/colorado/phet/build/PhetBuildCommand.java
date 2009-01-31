/* Copyright 2007, University of Colorado */
package edu.colorado.phet.build;

import scala.tools.ant.Scalac;

import java.io.*;
import java.util.Properties;

import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import edu.colorado.phet.build.proguard.PhetProguardConfigBuilder;
import edu.colorado.phet.build.proguard.ProguardCommand;
import edu.colorado.phet.build.util.PhetBuildUtils;

/**
 * This command builds a PhET project, together with any dependencies.
 */
public class PhetBuildCommand {
    private final PhetProject project;
    private final AntTaskRunner antTaskRunner;
    private final boolean shrink;
    private final File outputJar;

    //select whether you want to use the java version checker for launching JAR files
    private static boolean useJavaVersionChecker = false;
    private static String JAVA_SOURCE_VERSION = "1.4";//used for sims, not for bootstrap

    public static final String JAR_LAUNCHER = useJavaVersionChecker ?
                                              "edu.colorado.phet.javaversionchecker.JavaVersionChecker" :
                                              "edu.colorado.phet.common.phetcommon.application.JARLauncher";

    public static String getMainLauncherClassName() {
        return JAR_LAUNCHER;
    }

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
        compileJava();
        if ( project.containsScalaSource() ) {
            compileScala();
        }
    }

    private void compileScala() {
        Scalac scalac = new Scalac();
        String s = new File( project.getProjectDir(), "../../build-tools/scala/scala-library.jar" ).getAbsolutePath();
        System.out.println( "s = " + s );
        scalac.setClasspath( new Path( antTaskRunner.getProject(), toString( project.getAllJarFiles() ) + " : " + project.getClassesDirectory().getAbsolutePath() + " : " + s ) );
        scalac.setSrcdir( new Path( antTaskRunner.getProject(), toString( project.getAllScalaSourceRoots() ) ) );
        scalac.setTarget( "jvm-1.4" );//see Scalac.Target
        scalac.setDestdir( project.getClassesDirectory() );
        antTaskRunner.runTask( scalac );
        System.out.println( "Finished scala build." );
    }

    private void compileJava() {
        if ( useJavaVersionChecker ) {
            compileJavaVersionChecker();
        }

        File[] src = project.getAllJavaSourceRoots();
        File[] classpath = project.getAllJarFiles();

        PhetBuildUtils.antEcho( antTaskRunner, "Compiling " + project.getName() + ".", getClass() );

        Javac javac = new Javac();
        javac.setSource( JAVA_SOURCE_VERSION );
        javac.setTarget( JAVA_SOURCE_VERSION );
        javac.setSrcdir( new Path( antTaskRunner.getProject(), toString( src ) ) );
        javac.setDestdir( project.getClassesDirectory() );
        javac.setClasspath( new Path( antTaskRunner.getProject(), toString( classpath ) ) );

        //"lines,source" appears to be necessary to get line number debug info
        javac.setDebugLevel( "lines,source" );
        javac.setDebug( true );

        antTaskRunner.runTask( javac );

        PhetBuildUtils.antEcho( antTaskRunner, "Finished compiling " + project.getName() + ".", getClass() );
    }

    private void compileJavaVersionChecker() {
        PhetBuildUtils.antEcho( antTaskRunner, "Compiling java version checker for " + project.getName() + ".", getClass() );

        Javac javac = new Javac();
        javac.setSource( "1.4" );
        javac.setTarget( "1.4" );
        javac.setClasspath( new Path( antTaskRunner.getProject(), "contrib/javaws/jnlp.jar" ) );
        javac.setSrcdir( new Path( antTaskRunner.getProject(), "common/java-version-checker/src" ) );
        javac.setDestdir( project.getClassesDirectory() );
        javac.setDebugLevel( "lines,source" );
        javac.setDebug( true );

        antTaskRunner.runTask( javac );

        PhetBuildUtils.antEcho( antTaskRunner, "Finished compiling java version checker for" + project.getName() + ".", getClass() );
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

        //need to use simulation launcher if there are multiple simulation or if any simulation contains args
        //for now, let's just use JarLauncher for all usages.

        attribute.setName( "Main-Class" );
        //todo: support a main-class chooser & launcher
        attribute.setValue( JAR_LAUNCHER );

        jar.addFileset( toFileSet( createProjectPropertiesFile() ) );
        jar.addFileset( toFileSet( createLicenseInfoFile() ) );

        manifest.addConfiguredAttribute( attribute );
        jar.addConfiguredManifest( manifest );

        antTaskRunner.runTask( jar );
    }

    private File createLicenseInfoFile() {
        File file = new File( project.getAntOutputDir(), "license-info.txt" );
        file.getParentFile().mkdirs();
        try {
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( file ) );
            bufferedWriter.write( "#This file identifies licenses of contibuted libraries\n" );
            PhetProject[] dep = project.getAllDependencies();
            for ( int i = 0; i < dep.length; i++ ) {
                PhetProject phetProject = dep[i];
                bufferedWriter.write(phetProject.getLicensingInfo().toString());
            }
            bufferedWriter.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        return file;
    }

    private FileSet toFileSet( File file ) {
        FileSet fileSet = new FileSet();
        fileSet.setFile( file );
        return fileSet;
    }

    /*
    * Creates a properties file that describes things about the project.
    */
    private File createProjectPropertiesFile() {

        // create the various properties
        Properties properties = new Properties();
        properties.setProperty( "project.name", project.getName() );
        for ( int i = 0; i < project.getSimulations().length; i++ ) {
            Simulation simulation = project.getSimulations()[i];
            properties.setProperty( "project.flavor." + simulation.getName() + ".mainclass", simulation.getMainclass() );
            properties.setProperty( "project.flavor." + simulation.getName() + ".title", simulation.getTitle() );
            String args = "";
            String[] a = simulation.getArgs();
            for ( int j = 0; j < a.length; j++ ) {
                args += a[j] + " ";
            }
            properties.setProperty( "project.flavor." + simulation.getName() + ".args", args.trim() );
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
