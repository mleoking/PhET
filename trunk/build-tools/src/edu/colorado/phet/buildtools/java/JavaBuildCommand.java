/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.taskdefs.SignJar;
import org.apache.tools.ant.taskdefs.VerifyJar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import scala.tools.ant.Scalac;
import edu.colorado.phet.buildtools.AntTaskRunner;
import edu.colorado.phet.buildtools.PhetCleanCommand;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.proguard.PhetProguardConfigBuilder;
import edu.colorado.phet.buildtools.proguard.ProguardCommand;
import edu.colorado.phet.buildtools.util.PhetBuildUtils;
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
    
    // these keys must be in a properties file
    public static final String KEY_KEYSTORE = "jarsigner.keystore";
    public static final String KEY_PASSWORD = "jarsigner.password";
    public static final String KEY_ALIAS = "jarsigner.alias";

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
    	
    	// Obtain the properties needed for signing the JAR.
    	
        File configProperties = new File( project.getTrunk(), "build-tools/build-local.properties" );
        Properties properties = new Properties();
        try {
            properties.load( new FileInputStream( configProperties ) );
        }
        catch( IOException e ) {
        	System.out.println("Error: Property file needed for signing JAR not found.");
            e.printStackTrace();
            throw new BuildException("Property file needed for signing JAR not found.");
        }
        
        String alias = properties.getProperty( KEY_ALIAS );
        String keystore = properties.getProperty( KEY_KEYSTORE );
        String storepass = properties.getProperty( KEY_PASSWORD );
        
        // All properties must be present.
        if ( (alias == null) || (keystore == null) || (storepass == null) ){
        	throw new BuildException("One or more properties needed for signing JAR file not found.");
        }
        
        // Do the actual signing.
        
        SignJar signer = new SignJar();
        signer.setAlias( alias );
        signer.setKeystore( keystore );
        signer.setStoretype( "pkcs12" );
        signer.setStorepass( storepass );
        signer.setJar( outputJar );
        signer.setVerbose( true );
        
        antTaskRunner.runTask( signer );
        
        // Verify that the signing succeeded.
        
        VerifyJar verifier = new VerifyJar();
        verifier.setVerbose( true );
        verifier.setJar( outputJar );
        
        antTaskRunner.runTask( verifier );
        
        System.out.println("Signing of JAR file completed.");
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
