
package edu.colorado.phet.common.phetcommon.files;

import java.io.File;

import edu.colorado.phet.common.phetcommon.util.AbstractPropertiesFile;
import edu.colorado.phet.common.phetcommon.util.DeploymentScenario;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 *
 * Encapsulates the properties file that is written by the PhET installer.
 * This file contains info about the installer and the installation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetInstallationPropertiesFile extends AbstractPropertiesFile {
    
    private static PhetInstallationPropertiesFile instance;
    
    /**
     * Gets the instance of this object.
     * @return
     * @throws IllegalStateException if not running from a PhET installation
     */
    public static PhetInstallationPropertiesFile getInstance() throws IllegalStateException {
        if ( DeploymentScenario.getInstance() != DeploymentScenario.PHET_INSTALLATION ) {
            throw new IllegalStateException( "object cannot be created unless running from a PhET installation" );
        }
        if ( instance == null ) {
            instance = new PhetInstallationPropertiesFile();
        }
        return instance;
    }
    
    /* singleton */
    private PhetInstallationPropertiesFile() {
        super( getFile() );
    }
    
    /**
     * Timestamp that indicates when the installer was created,
     * in seconds since Epoch.
     * @return
     */
    public int getInstallerCreationTimestamp() {
        return getPropertyInt( "installer.creation.date.epoch.seconds", -1 );
    }
    
    /**
     * Timestamp that indicates when the installation was performed,
     * in seconds since Epoch.
     * @return
     */
    public int getInstallationTimestamp() {
        return getPropertyInt( "install.date.epoch.second", -1 );
    }

    /*
     * The properties file lives 1 directory above where the sim's JAR file lives.
     */
    private static File getFile() {
        return new File( FileUtils.getCodeSource().getParentFile().getParentFile(), "phet-installation.properties" );
    }
    
    public static void main( String[] args ) {
        System.setProperty( "javaws.deployment.scenario", DeploymentScenario.PHET_INSTALLATION.getName() );
        PhetInstallationPropertiesFile p = PhetInstallationPropertiesFile.getInstance();
        System.out.println( p.getInstallerCreationTimestamp() );
        System.out.println( p.getInstallationTimestamp() );
    }
}
