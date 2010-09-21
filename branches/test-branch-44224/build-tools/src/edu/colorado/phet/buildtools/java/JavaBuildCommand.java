/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.java;

import scala.tools.ant.Scalac;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.taskdefs.ManifestException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import edu.colorado.phet.buildtools.*;
import edu.colorado.phet.buildtools.proguard.PhetProguardConfigBuilder;
import edu.colorado.phet.buildtools.proguard.ProguardCommand;
import edu.colorado.phet.buildtools.util.FileUtils;
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

    //select whether you want to use the java version checker for launching JAR files
    private static boolean useJavaVersionChecker = true;

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
        long executeStartTime = System.currentTimeMillis();//Time tracking, may be removed later
        clean();
        compile();
        copySoftwareAgreement();
        project.copyLicenseInfo();
        jar();
        long proguardStartTime = System.currentTimeMillis();//Time tracking, may be removed later
        proguard();
        long proguardEndTime = System.currentTimeMillis();//Time tracking, may be removed later
        if (BuildLocalProperties.getInstance().isJarsignerCredentialsSpecified()) {
            if (project.getSignJar()) {
                signJAR();
            }
        } else {
            System.out.println("Jarsigner credentials not specified in build file, skipping jar signing.");
        }
        
        //Time tracking, may be removed later
        long executeEndTime = System.currentTimeMillis();
        double proguardPercent = ((double)(proguardEndTime-proguardStartTime))/(executeEndTime-executeStartTime)*100.0;
        System.out.println("timing "+project.getName()+": proguardTime = "+(proguardEndTime-proguardStartTime)+" ms");
        System.out.println("timing "+project.getName()+": execTime = "+(executeEndTime-executeStartTime)+" ms");
        System.out.println("timing "+project.getName()+": proguard took "+proguardPercent+"%");
    }

    public void copySoftwareAgreement() {
        File src = new File( project.getTrunk(), BuildToolsPaths.SOFTWARE_AGREEMENT_PATH );
        try {
            FileUtils.copyRecursive( src, getSoftwareAgreementDir() );
//            SetSVNIgnoreToDeployDirectories.setIgnorePatternsOnDir( getSoftwareAgreementDir().getParentFile(), new String[]{getSoftwareAgreementDir().getName()} );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private File getSoftwareAgreementDir() {
        //Copy the software agreement to the top level, so it will appear at the top level of the jar file
        return new File( project.getTrunk(), BuildToolsPaths.PHETCOMMON_DATA );
    }

    private void signJAR() {
        PhetJarSigner signer = new PhetJarSigner( BuildLocalProperties.getInstance() );
        // Sign the JAR.
        if ( signer.signJar( outputJar ) != true ) {
            // Signing failed.  Throw an exception in order to force the build process to stop.
            throw new BuildException( "Signing of JAR file failed." );
        }
    }

    private void clean() throws Exception {
        new PhetCleanCommand( project, antTaskRunner ).execute();
    }

    private void compile() {
        if ( project.containsScalaSource() ) {
            compileScala();
        }
        compileJava();
    }

    //Joint compilation for java/scala mixed projects
    //see http://www.codecommit.com/blog/scala/joint-compilation-of-scala-and-java-sources
    private void compileScala() {
        Scalac scalac = new Scalac();
        scalac.setClasspath( new Path( antTaskRunner.getProject(), project.getClasspath() +
                                                                   " : " + project.getClassesDirectory().getAbsolutePath() ) );
        ArrayList<File> all = new ArrayList<File>( Arrays.asList( project.getAllScalaSourceRoots() ) );
        all.addAll( Arrays.asList( project.getAllJavaSourceRoots() ) );
        scalac.setSrcdir( new Path( antTaskRunner.getProject(), toClasspathString( all.toArray( new File[all.size()] ) ) ) );
        scalac.setIncludes( "**/*.scala, **/*.java" );
        scalac.setTarget( BuildToolsConstants.SIM_SCALA_VERSION );//see Scalac.Target, allows targeting 1.4 jvm
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
        javac.setIncludeantruntime(false);//see #2431, should not include full classpath if running from IDE
        javac.setSource( project.getJavaSourceVersion() );
        javac.setTarget( project.getJavaTargetVersion() );
        javac.setSrcdir( new Path( antTaskRunner.getProject(), toClasspathString( src ) ) );
        javac.setDestdir( project.getClassesDirectory() );

        //This block enables compilation of mixed java-scala sources by pointing the java compiler at the compiled scala source
        // see http://www.codecommit.com/blog/scala/joint-compilation-of-scala-and-java-sources
        if ( project.containsScalaSource() ) {
            ArrayList<File> all = new ArrayList<File>( Arrays.asList( classpath ) );
            all.add( project.getClassesDirectory() );
            classpath = all.toArray( new File[all.size()] );
        }
        
        javac.setClasspath( new Path( antTaskRunner.getProject(), project.getClasspath() ) );

        //"lines,source" appears to be necessary to get line number debug info
        javac.setDebugLevel( "lines,source" );
        javac.setDebug( true );

        antTaskRunner.runTask( javac );

        PhetBuildUtils.antEcho( antTaskRunner, "Finished compiling " + project.getName() + ".", getClass() );
    }

    private void compileJavaVersionChecker() {
        PhetBuildUtils.antEcho( antTaskRunner, "Compiling java version checker for " + project.getName() + ".", getClass() );

        Javac javac = new Javac();
        javac.setIncludeantruntime(false);//see #2431, should not include full classpath if running from IDE
        javac.setSource( BuildToolsConstants.BOOTSTRAP_JAVA_VERSION );//Java version checker must be compiled in lowest language version
        javac.setTarget( BuildToolsConstants.BOOTSTRAP_JAVA_VERSION );//so it can run in lowest language version jvms
        javac.setClasspath( new Path( antTaskRunner.getProject(), new File( project.getTrunk(), BuildToolsPaths.JNLP_JAR ).getAbsolutePath() ) );
        javac.setSrcdir( new Path( antTaskRunner.getProject(), new File( project.getTrunk(), BuildToolsPaths.JAVA_COMMON + "/java-version-checker/src" ).getAbsolutePath() ) );
        javac.setDestdir( project.getClassesDirectory() );
        javac.setDebugLevel( "lines,source" );
        javac.setDebug( true );

        //if this fails, you may need to add Sun's tools.jar to you working copy (ignored from svn for legal reasons)
        //see trunk/build-tools/contrib/sun-tools/readme.txt
        antTaskRunner.runTask( javac );

        PhetBuildUtils.antEcho( antTaskRunner, "Finished compiling java version checker for " + project.getName() + ".", getClass() );
    }

    protected void jar() throws ManifestException {
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

    protected FileSet toFileSetFile( File file ) {
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

    protected void proguard() throws Exception {
        PhetProguardConfigBuilder builder = new PhetProguardConfigBuilder();

        builder.setDestJar( outputJar );
        builder.setJavaProject( project );
        builder.setShrink( shrink );

        new ProguardCommand( builder.build(), antTaskRunner ).execute();
    }

    public static String toClasspathString( File[] files ) {
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
