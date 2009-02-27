/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.java;

import scala.tools.ant.Scalac;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import edu.colorado.phet.buildtools.AntTaskRunner;
import edu.colorado.phet.buildtools.PhetCleanCommand;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.proguard.PhetProguardConfigBuilder;
import edu.colorado.phet.buildtools.proguard.ProguardCommand;
import edu.colorado.phet.buildtools.util.PhetBuildUtils;
import edu.colorado.phet.buildtools.util.PhetJarSigner;
import edu.colorado.phet.common.phetcommon.application.JARLauncher;

/**
 * This command builds a PhET project, together with any dependencies.
 */
public class JavaBuildCommand {
    private final JavaProject project;
    private final AntTaskRunner antTaskRunner;
    private final boolean shrink;
    private final File outputJar;

    private static String JAR_LAUNCHER_PROPERTIES_FILE_HEADER = "created by " + JavaBuildCommand.class.getName();

    private static String JAVA_SOURCE_VERSION = "1.4";//used for sims, not for bootstrap

    //select whether you want to use the java version checker for launching JAR files
    private static boolean useJavaVersionChecker = false;

    public static final String JAVA_VERSION_CHECKER_CLASS_NAME = "edu.colorado.phet.javaversionchecker.JavaVersionChecker";
    public static final String JAR_LAUNCHER_CLASS_NAME = JARLauncher.class.getName();

    public static String getMainLauncherClassName( JavaProject project ) {
        if ( project.getAlternateMainClass() != null ) {
            return project.getAlternateMainClass();
        }
        return ( useJavaVersionChecker ? JAVA_VERSION_CHECKER_CLASS_NAME : JAR_LAUNCHER_CLASS_NAME );
    }

    public JavaBuildCommand( JavaProject project, AntTaskRunner taskRunner, boolean shrink, File outputJar ) {
        this.project = project;
        this.antTaskRunner = taskRunner;
        this.shrink = shrink;
        this.outputJar = outputJar;
    }

    public void execute() throws Exception {
        clean();
        compile();
        project.copyLicenseInfo();
        jar();
        proguard();
        if ( project.getSignJar() ) {
            signJAR();
        }
    }

    private void signJAR() {
        File configProperties = new File( project.getTrunk(), "build-tools/build-local.properties" );
        Properties properties = new Properties();
        try {
            properties.load( new FileInputStream( configProperties ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        PhetJarSigner signer = new PhetJarSigner( configProperties.getAbsolutePath() );
        boolean result = signer.signJar( outputJar.getAbsolutePath() );

        System.out.println( "Done, signing result = " + result + "." );
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
        String s = null;
        try {
            s = new File( project.getProjectDir(), "../../build-tools/scala/scala-library.jar" ).getCanonicalPath();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
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
        //TODO: support a main-class chooser & launcher
        attribute.setValue( getMainLauncherClassName( project ) );

        jar.addFileset( toFileSetFile( createJARLauncherPropertiesFile() ) );

        manifest.addConfiguredAttribute( attribute );
        jar.addConfiguredManifest( manifest );

        antTaskRunner.runTask( jar );
    }

    private FileSet toFileSetFile( File file ) {
        FileSet fileSet = new FileSet();
        fileSet.setFile( file );
        return fileSet;
    }

    /*
    * Creates a properties file that tells JARLauncher what to run.
    */
    private File createJARLauncherPropertiesFile() {

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
        File file = new File( project.getAntOutputDir(), JARLauncher.PROPERTIES_FILE_NAME );
        file.getParentFile().mkdirs();
        try {
            properties.store( new FileOutputStream( file ), JAR_LAUNCHER_PROPERTIES_FILE_HEADER );
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
        builder.setJavaProject( project );
        builder.setShrink( shrink );

        new ProguardCommand( builder.build(), antTaskRunner ).execute();
    }

    private String toString( File[] files ) {
        String string = "";
        for ( int i = 0; i < files.length; i++ ) {
            File file = files[i];
            try {
                string += file.getCanonicalPath();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            if ( i < files.length - 1 ) {
                string += " : ";
            }
        }
        return string;
    }
}
