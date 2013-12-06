package edu.colorado.phet.buildtools.java;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildToolsConstants;
import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.MyAntTaskRunner;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.preprocessor.ResourceGenerator;
import edu.colorado.phet.buildtools.util.BuildPropertiesFile;
import edu.colorado.phet.buildtools.util.PhetBuildUtils;
import edu.colorado.phet.buildtools.util.PhetJarSigner;
import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public abstract class JavaProject extends PhetProject {
    public JavaProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public JavaProject( File parentDir, String name ) throws IOException {
        super( parentDir, name );
    }

    public Locale[] getLocales() {
        return getLocalesImpl( ".properties" );
    }

    public static PhetProject[] getJavaSimulations( File trunk ) {
        File simulationsJava = new File( trunk, BuildToolsPaths.SIMULATIONS_JAVA );
        Collection projects = new ArrayList();
        String[] sims = getSimNames( simulationsJava );
        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];
            File projectDir = PhetBuildUtils.resolveProject( simulationsJava, sim );
            try {
                PhetProject phetProject = new JavaSimulationProject( projectDir, sim );
                projects.add( phetProject );
            }
            catch ( IOException e ) {
                throw new BuildException( e );
            }
        }

        projects = PhetProject.sort( new ArrayList( projects ) );
        return (PhetProject[]) projects.toArray( new PhetProject[projects.size()] );
    }

    public boolean build() throws Exception {
        if ( getBuildPropertiesFileObject().getGenerateResourceFile() ) {
            new ResourceGenerator( getTrunk() ).generateResources( getProjectDir() );
        }
        else {
            System.out.println( "Skipping automatic resource generation during build" );
        }
        new JavaBuildCommand( this, new MyAntTaskRunner(), isShrink(), this.getDefaultDeployJar() ).execute();
        File[] f = getDeployDir().listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.getName().toLowerCase().endsWith( ".jar" );
            }
        } );
        return f.length == 1;//success if there is exactly one jar
    }

    public boolean isShrink() {
        return true;
    }

    public File getTranslationFile( Locale locale ) {
        String localeString = LocaleUtils.localeToString( locale );
        String lang = localeString.equals( "en" ) ? "" : "_" + localeString; // JO: fixed locale bug
        return new File( getProjectDir(), "data" + File.separator + getName() + File.separator + "localization" + File.separator + getName() + "-strings" + lang + ".properties" );
    }

    public String getListDisplayName() {
        return getName();
    }

    public void runSim( Locale locale, String simulationName ) {
        Java java = new Java();

        java.setClassname( getSimulation( simulationName ).getMainclass() );
        java.setFork( true );
        String args = "-dev";
        String[] a = getSimulation( simulationName ).getArgs();
        for ( int i = 0; i < a.length; i++ ) {
            String s = a[i];
            args += " " + s;
        }
        java.setArgs( args );

        org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
        project.init();

        Path classpath = new Path( project );
        FileSet set = new FileSet();
        set.setFile( getDefaultDeployJar() );
        classpath.addFileset( set );
        java.setClasspath( classpath );

        String language = locale.getLanguage();
        if ( !language.equals( "en" ) ) {
            java.setJvmargs( "-D" + PhetCommonConstants.PROPERTY_PHET_LANGUAGE + "=" + language );
            java.setJvmargs( "-Djavaws.phet.locale=" + language ); //XXX #1057, backward compatibility, delete after IOM
        }

        File file = new File( getTrunk(), BuildToolsPaths.BUILD_TOOLS_DIR + "/test-output.txt" );
        java.setOutput( file );

        System.out.println( "Launching task, output will be printed after finish." );
        new MyAntTaskRunner().runTask( java );
        try {
            String text = FileUtils.loadFileAsString( file );
            System.out.println( "Process finished:\n" + text );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Load the simulation for associated with this project for the specified name and locale.
     * TODO: better error handling for missing attributes (for projects that don't support simulations yet)
     *
     * @param simulationName
     * @return
     */
    public Simulation getSimulation( String simulationName, Locale locale ) {
        BuildPropertiesFile buildPropertiesFile = super.getBuildPropertiesFileObject();
        String mainclass = buildPropertiesFile.getMainClass( simulationName );
        if ( mainclass == null ) {
            mainclass = buildPropertiesFile.getMainClassDefault();
        }
        if ( mainclass == null ) {
            throw new RuntimeException( "Mainclass was null for project=" + getName() + ", simulation=" + simulationName );
        }
        String[] args = buildPropertiesFile.getArgs( simulationName );

        //If we reuse PhetResources class, we should move Proguard usage out, so GPL doesn't virus over
        Properties localizedProperties = new Properties();
        try {
            File localizationFile = getLocalizationFile( locale );
            String title = null;
            if ( localizationFile.exists() ) {
                localizedProperties.load( new FileInputStream( localizationFile ) );//TODO: handle locale (graceful support for missing strings in locale)
                String titleKey = simulationName + ".name";
                title = localizedProperties.getProperty( titleKey );
                if ( title == null ) {
                    Properties englishProperties = new Properties();
                    FileInputStream inputStream = new FileInputStream( getLocalizationFile( new Locale( "en" ) ) );
                    englishProperties.load( inputStream );
                    title = englishProperties.getProperty( titleKey );
                    System.out.println( "PhetProject.getSimulation: missing title for simulation: key=" + titleKey + ", locale=" + locale + ", using English" );
                    if ( title == null ) {
                        title = simulationName;
                    }
                    inputStream.close();
                }
            }
            else {
                System.out.println( "PhetProject.getSimulation: localization file doesn't exist: " + localizationFile.getAbsolutePath() );
                title = getName();
            }

            return new Simulation( simulationName, title, mainclass, args, buildPropertiesFile.requestAllPermissions( simulationName ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public void buildJNLP( String codebase, boolean dev ) {
        String[] simulationNames = getSimulationNames();
        Locale[] locales = getLocales();
        for ( int i = 0; i < locales.length; i++ ) {
            Locale locale = locales[i];

            for ( int j = 0; j < simulationNames.length; j++ ) {
                String simulationName = simulationNames[j];
                try {
                    buildJNLP( locale, simulationName, codebase, dev );
                }
                catch ( Exception e ) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    public void buildJNLP( Locale locale, String simulationName, String codebase, boolean dev ) throws Exception {
        System.out.println( "Building JNLP for locale=" + locale + ", simulation=" + simulationName );
        BuildJNLPTask j = new BuildJNLPTask();
        // generate the production JNLP
        j.buildJNLP( this, simulationName, locale, false /* dev */, false /* interviews */, codebase );
        // generate dev and interviews JNLPs for English
        if ( dev && locale.equals( LocaleUtils.stringToLocale( "en" ) ) ) {
            j.buildJNLP( this, simulationName, locale, true /* dev */, false /*interviews */, codebase, "-dev" );
            j.buildJNLP( this, simulationName, locale, false /* dev */, true /*interviews */, codebase, "-interviews" );
        }
        System.out.println( "Finished Building JNLP" );
    }

    public String getLaunchFileSuffix() {
        return "jnlp";
    }

    public boolean getSignJar() {
        return false;
    }

    public File getLocalizationFile( Locale locale ) {
        String suffix = locale.equals( new Locale( "en" ) ) ? "" : "_" + locale;
        return new File( getLocalizationDir(), getName() + "-strings" + suffix + ".properties" );
    }

    private String getJarName() {
        return PhetApplicationConfig.getProjectJarName( getName() );
    }

    public File getDefaultDeployJar() {
        return new File( getDeployDir(), getJarName() );
    }

    public File getJarFile() {
        File file = new File( getAntOutputDir(), "jars/" + getJarName() );
        file.getParentFile().mkdirs();
        return file;
    }

    public String getJavaSourceVersion() {
        return BuildToolsConstants.SIM_JAVA_VERSION;
    }

    public String getJavaTargetVersion() {
        return BuildToolsConstants.SIM_JAVA_VERSION;
    }

    /*
     * See #3016.
     * If there is a project-specific proguard configuration file, return it.
     * Otherwise return an empty array.
     */
    //TODO #3030, collect proguard config files for all dependencies
    public File[] getAdditionalProguardConfigFiles() {
        File proguardConfigFile = new File( getProjectDir(), getName() + ".pro" );
        if ( proguardConfigFile.exists() ) {
            return new File[] { proguardConfigFile };
        }
        else {
            return new File[0];
        }
    }

    /**
     * @return Whether the project depends on JMonkeyEngine 3
     */
    public boolean containsJME3Dependency() {
        for ( File file : getAllJarFiles() ) {
            if ( file.getAbsolutePath().contains( "jme3" ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Whether the project depends on LWJGL (directly. the indirect dependency via JME3 should return false).
     */
    public boolean containsLWJGLDependency() {
        for ( File file : getAllJarFiles() ) {
            if ( file.getAbsolutePath().contains( "lwjgl" ) ) {
                return true;
            }
        }
        return false;
    }

    public String getAdditionalJnlpResources() {
        if ( containsJME3Dependency() || containsLWJGLDependency() ) {
            System.out.println( "Contains JME3/LWJGL, adding in addition JNLP resources" );
            try {
                return FileUtils.loadFileAsString( new File( getTrunk(), BuildToolsPaths.LWJGL_JNLP_RESOURCES ) );
            }
            catch ( IOException e ) {
                throw new RuntimeException( "Problem with JME3/LWJGL JNLP resource file", e );
            }
        }
        else {
            return "";
        }
    }

    @Override public void copyAssets() throws IOException {
        super.copyAssets();

        // copy over LWJGL native libraries to our deploy directory, so that they can be accessed online through their JNLP dependencies
        if ( containsJME3Dependency() || containsLWJGLDependency() ) {
            // pick the variety of native libs that we use depending on whether it is JME3 or straight LWJGL
            File nativesDir = new File( getTrunk(), containsJME3Dependency() ? BuildToolsPaths.JME3_NATIVES : BuildToolsPaths.LWJGL_NATIVES );

            PhetJarSigner jarSigner = new PhetJarSigner( BuildLocalProperties.getInstance() );

            System.out.println( "Copying LWJGL native JARs into the deploy directory" );
            for ( File file : nativesDir.listFiles( new FilenameFilter() {
                public boolean accept( File dir, String name ) {
                    return name.startsWith( "native_" ) && name.endsWith( ".jar" );
                }
            } ) ) {
                File destinationFile = new File( getDeployDir(), file.getName() );
                FileUtils.copyTo( file, destinationFile );

                jarSigner.signJar( null, destinationFile );
            }
        }
    }
}