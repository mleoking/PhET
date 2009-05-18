package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FilenameFilter;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.buildtools.util.FileUtils;

public class ResourceDeployServer implements IProguardKeepClass {

    private String jarCommand;
    private File buildLocalProperties;
    private File resourceDir;
    
    private File resourceFile;
    private String resourceDestination;
    private String[] sims;
    private File backupDir;

    public ResourceDeployServer( String jarCommand, File buildLocalProperties, File resourceDir ) {
        this.jarCommand = jarCommand;
        this.buildLocalProperties = buildLocalProperties;
        this.resourceDir = resourceDir;

        File propertiesFile = new File( resourceDir, "resource/resource.properties" );
        Properties properties = new Properties();

        try {
            properties.load( new FileInputStream( propertiesFile ) );
        }
        catch( IOException e ) {
            System.out.println( "error: could not load resource properties file" );
            e.printStackTrace();
            return;
        }

        String resourceFilename = properties.getProperty( "resourceFile" );
        resourceFile = new File( resourceFilename );

        if( !resourceFile.exists() ) {
            System.out.println( "Cannot locate resource file, aborting" );
            return;
        }

        resourceDestination = properties.getProperty( "resourceDestination" );

        String simsString = properties.getProperty( "sims" );
        sims = simsString.split( "," );

        backupDir = new File( resourceDir, "backup" );
        backupDir.mkdir();

        try {
            createBackupJARs();

            

        }
        catch( IOException e ) {
            e.printStackTrace();
        }

    }

    private void createBackupJARs() throws IOException {
        File liveDir = getLiveSimsDir();
        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];

            File simDir = new File( liveDir, sim );

            if( !simDir.exists() ) {
                System.out.println( "WARNING: skipping sim dir " + simDir.getCanonicalPath() + ", does not exist" );
                continue;
            }

            File[] jarFiles = simDir.listFiles( new FilenameFilter() {
                public boolean accept( File file, String name ) {
                    return name.endsWith( ".jar" ); 
                }
            });

            if( jarFiles.length == 0 ) {
                System.out.println( "WARNING: no JARs found in sim dir: " + simDir.getCanonicalPath() );
                continue;
            }

            File simBackupDir = new File( backupDir, sim );
            simBackupDir.mkdir();

            for ( int j = 0; j < jarFiles.length; j++ ) {
                File jarFile = jarFiles[j];

                FileUtils.copyToDir( jarFile, simBackupDir );
            }
        }
    }

    public File getLiveSimsDir() {
        return new File( resourceDir, "../../../sims" );
    }

    public static void main( String[] args ) {
        System.out.println( "Running ResourceDeployServer" );

        new ResourceDeployServer( args[0], new File( args[1] ), new File( args[2] ) );

    }

}
