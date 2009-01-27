package edu.colorado.phet.common.phetcommon.util;

import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.jar.JarFile;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

/**
 * PhET simulations can be deployed in several different ways.
 * This class determines how the sim that is running was deployed.
 * <p>
 * This implementation combines the singleton pattern and the Java 1.4 typesafe enum pattern.
 */
public class DeploymentScenario {
    
    // enumeration
    public static final DeploymentScenario PHET_INSTALLATION = new DeploymentScenario( "phet-installation" );
    public static final DeploymentScenario STANDALONE_JAR = new DeploymentScenario( "standalone-jar" );
    public static final DeploymentScenario PHET_WEBSITE = new DeploymentScenario( "phet-website" );
    public static final DeploymentScenario OTHER_WEBSITE = new DeploymentScenario( "other-website" );
    public static final DeploymentScenario UNKNOWN = new DeploymentScenario( "unknown" );

    // singleton
    private static DeploymentScenario instance = null;
    
    private final String name;
    
    /* singleton */
    private DeploymentScenario( String name ) {
        this.name = name;
    }
    
    public String toString() {
        return name;
    }
    
    /**
     * Gets the deployment scenario, a singleton.
     * This is determined once, on demand, since it does not change.
     * @return
     */
    public static DeploymentScenario getInstance() {
        if ( instance == null ) {
            instance = determineScenario();
        }
        return instance;
    }
    
    private static DeploymentScenario determineScenario() {
        DeploymentScenario scenario = DeploymentScenario.UNKNOWN;
        if ( isPhetInstallation() ) {
            scenario = DeploymentScenario.PHET_INSTALLATION;
        }
        else if ( isStandaloneJar() ) {
            scenario = DeploymentScenario.STANDALONE_JAR;
        }
        else if ( isWebsite() ) {
            scenario = DeploymentScenario.PHET_WEBSITE;
        }
        return scenario;
    }
    
    /*
     * Was this sim run as part of a PhET installation, created using the PhET installer?
     * If it was, then a file named .phet-installer will live in the JAR's parent directory.
     * 
     * @return true or false
     */
    private static boolean isPhetInstallation() {
        boolean isPhetInstallation = false;
        if ( isJarCodeSource() ) {
            File codeSource = FileUtils.getCodeSource();
            File parent = codeSource.getParentFile();
            if ( parent != null ) {
                File grandparent = parent.getParentFile();
                if ( grandparent != null ) {
                    File specialFile = new File( grandparent.getAbsolutePath() + System.getProperty( "file.separator" ) + "phet-installation.properties" );
                    isPhetInstallation = specialFile.exists();
                }
            }
        }
        return isPhetInstallation;
    }

    /*
     * Is this sim running from a stand-alone JAR file on the user's local machine?
     * @return
     */
    private static boolean isStandaloneJar() {
        return !PhetServiceManager.isJavaWebStart() && !isPhetInstallation() && isJarCodeSource();
    }
    
    /*
     * Is this sim running from a web site using Java Web Start?
     * @return
     */
    private static boolean isWebsite() {
        return PhetServiceManager.isJavaWebStart() && !isPhetInstallation(); // PhET installer uses Web Start!
    }
    
    /*
     * Is the code source a JAR file?
     * @return
     */
    private static boolean isJarCodeSource() {
        //TODO: bad style to write code that depends on exceptions
        try {
            return isJar( FileUtils.getCodeSource() );
        }
        catch( AccessControlException ace ) {
            return false;
        }
    }
    
    /*
     * Is the file a JAR?
     * @param file
     * @return
     */
    private static boolean isJar( File file ) {
        //TODO: bad style to write code that depends on exceptions
        try {
            new JarFile( file ); // throws IOException if not a jar file
            return true;
        }
        catch ( IOException e ) {
            return false;
        }
    }
}
