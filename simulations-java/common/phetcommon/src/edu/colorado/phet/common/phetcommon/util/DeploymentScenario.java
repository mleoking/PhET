package edu.colorado.phet.common.phetcommon.util;

import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.jar.JarFile;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

/**
 * PhET simulations can be deployed in several different ways.
 * This class determines how the sim that is running was deployed.
 */
public class DeploymentScenario {
    
    private static final String NAME_PHET_INSTALLATION = "phet-installation";
    private static final String NAME_STANDALONE_JAR = "standalone-jar";
    private static final String NAME_WEBSITE = "website";
    private static final String NAME_UNKNOWN = "unknown";
    
    /* not intended for instantiation */
    private DeploymentScenario() {}

    /**
     * Gets the name of the deployment scenario.
     * 
     * @return
     */
    public static String getName() {
        String name = NAME_UNKNOWN;
        if ( isPhetInstallation() ) {
            name = NAME_PHET_INSTALLATION;
        }
        else if ( isStandaloneJar() ) {
            name = NAME_STANDALONE_JAR;
        }
        else if ( isWebsite() ) {
            name = NAME_WEBSITE;
        }
        return name;
    }
    
    /**
     * Was this sim run as part of a PhET installation, created using the PhET installer?
     * If it was, then a file named .phet-installer will live in the JAR's parent directory.
     * 
     * @return true or false
     */
    public static boolean isPhetInstallation() {
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

    /**
     * Is this sim running from a stand-alone JAR file on the user's local machine?
     * @return
     */
    public static boolean isStandaloneJar() {
        return !PhetServiceManager.isJavaWebStart() && !isPhetInstallation() && isJarCodeSource();
    }
    
    /**
     * Is this sim running from a web site using Java Web Start?
     * @return
     */
    public static boolean isWebsite() {
        return PhetServiceManager.isJavaWebStart() && !isPhetInstallation(); // PhET installer uses Web Start!
    }
    
    /**
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
    
    /**
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
