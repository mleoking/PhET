
package edu.colorado.phet.common.phetcommon.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import edu.colorado.phet.common.phetcommon.resources.PhetProperties;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

/**
 *
 * Encapsulates the notion of a PhET offline website installation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetInstallation {
    
    private static final String PROPERTIES_FILENAME = "phet-installation.properties";
    
    private static PhetInstallation instance;
    
    // properties associated with the PhET installation
    private final PhetProperties properties;
    
    /**
     * Gets the instance of this object.
     * @return
     */
    public static PhetInstallation getInstance() {
        if ( instance == null ) {
            instance = new PhetInstallation();
        }
        return instance;
    }
    
    /* singleton */
    private PhetInstallation() {
        properties = readProperties();
    }
    
    /**
     * Timestamp that indicates when the installer was created,
     * in seconds since Epoch.
     * @return
     */
    public long getInstallerTimestamp() {
        return properties.getLong( "installer.creation.date.epoch.seconds", -1 );
    }
    
    /**
     * Timestamp that indicates when the installation was performed,
     * in seconds since Epoch.
     * @return
     */
    public long getInstallationTimestamp() {
        return properties.getLong( "install.date.epoch.seconds", -1 );
    }
    
    /**
     * Does a PhET installation exists that is associated with this sim?
     * @return
     */
    public static boolean exists() {
        boolean exists = false;
        File propertiesFile = getPropertiesFile();
        if ( propertiesFile != null ) {
            exists = propertiesFile.exists();
        }
        return exists;
    }
    
    /*
     * Reads the properties file, if it exists.
     */
    private static PhetProperties readProperties() {
        PhetProperties properties = new PhetProperties();
        File file = getPropertiesFile();
        if ( file != null && file.exists() ) {
            try {
                properties.load( new FileInputStream( file ) );
            }
            catch ( IOException e ) {
                // this will only happen if the file is corrupt
                e.printStackTrace();
            }
        }
        return properties;
    }
    
    /*
     * Gets the properties file associated the the PhET installation.
     * Returns null if the file isn't found.
     */
    private static File getPropertiesFile() {
        File file = null;
        URL codeBase = PhetServiceManager.getCodeBase();
        if ( codeBase != null && codeBase.getProtocol().equals( "file" ) ) {
            String dirname = codeBase.getPath();
            String project = new File( dirname ).getName(); // last name in path is project name
            String jarName = project + "_all.jar"; // jar file shares project name
            File jarFile = new File( dirname, jarName );
            if ( jarFile.exists() ) {
                File parent = jarFile.getParentFile();
                if ( parent != null ) {
                    File grandParent = parent.getParentFile();
                    if ( grandParent != null ) {
                        file = new File( grandParent.getAbsolutePath(), PROPERTIES_FILENAME );
                    }
                }
            }
        }
        return file;
    }
}
