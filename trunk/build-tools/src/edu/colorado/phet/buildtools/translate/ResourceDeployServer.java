package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FilenameFilter;
import java.util.Properties;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.PhetJarSigner;
import edu.colorado.phet.buildtools.BuildLocalProperties;

public class ResourceDeployServer implements IProguardKeepClass {

    private String jarCommand;
    private File buildLocalProperties;
    private File resourceDir;
    
    private File resourceFile;
    private String resourceDestination;
    private String[] sims;
    private File backupDir;
    private boolean onlyAllJARs;
    private File testDir;

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
        resourceFile = new File( resourceDir, "resource/" + resourceFilename );

        if( !resourceFile.exists() ) {
            System.out.println( "Cannot locate resource file, aborting" );
            return;
        }

        resourceDestination = properties.getProperty( "resourceDestination" );
        if( resourceDestination.startsWith( "/" ) ) {
            resourceDestination = resourceDestination.substring( 1 );
        }
        onlyAllJARs = properties.getProperty( "onlyAllJARs" ).equals( "true" );

        String simsString = properties.getProperty( "sims" );
        sims = simsString.split( "," );

        backupDir = new File( resourceDir, "backup" );
        backupDir.mkdir();

        try {
            createBackupJARs();
            copyTestJARs();
            pokeJARs();
            signJARs();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return;
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
            return;
        }

        System.out.println( "All successful!" );

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

    private void copyTestJARs() throws IOException, InterruptedException {
        testDir = new File( resourceDir, "test" );
        testDir.mkdir();

        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];

            File backupSimDir = new File( backupDir, sim );
            File testSimDir = new File( testDir, sim );
            testSimDir.mkdir();

            File[] jarFiles = backupSimDir.listFiles();

            for ( int j = 0; j < jarFiles.length; j++ ) {
                File jarFile = jarFiles[j];

                if( onlyAllJARs && !jarFile.getName().endsWith( "_all.jar" ) ) {
                    continue;
                }

                FileUtils.copyToDir( jarFile, testSimDir );
            }
        }
    }

    private void pokeJARs() throws IOException, InterruptedException {
        File tmpDir = new File( resourceDir, ".tmp" );
        tmpDir.mkdir();

        File holderDir = new File( tmpDir, resourceDestination );
        holderDir.mkdirs();

        FileUtils.copyToDir( resourceFile, holderDir );

        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];

            File testSimDir = new File( testDir, sim );

            File[] jarFiles = testSimDir.listFiles();

            for ( int j = 0; j < jarFiles.length; j++ ) {
                File jarFile = jarFiles[j];

                String command = jarCommand + " uf " + jarFile.getAbsolutePath() + " -C " + tmpDir.getAbsolutePath() + " " + resourceDestination + resourceFile.getName();
                runStringCommand( command );
            }
        }
    }

    private void signJARs() {
        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];

            File testSimDir = new File( testDir, sim );
            File[] jarFiles = testSimDir.listFiles();

            for ( int j = 0; j < jarFiles.length; j++ ) {
                File jarFile = jarFiles[j];

                signJAR( jarFile );
            }
        }
    }

    private void signJAR( File jarFile ) {
        PhetJarSigner phetJarSigner = new PhetJarSigner( BuildLocalProperties.initFromPropertiesFile( buildLocalProperties ) );
        phetJarSigner.signJar( jarFile );
    }

    public File getLiveSimsDir() {
        return new File( resourceDir, "../.." );
    }

    private void runStringCommand( String command ) throws IOException, InterruptedException {
        System.out.println( "Running command: " + command );
        Process p = Runtime.getRuntime().exec( command );
        new StreamReaderThread( p.getErrorStream(), "err>" ).start();
        new StreamReaderThread( p.getInputStream(), "" ).start();
        p.waitFor();
    }

    public static void main( String[] args ) {
        System.out.println( "Running ResourceDeployServer" );

        new ResourceDeployServer( args[0], new File( args[1] ), new File( args[2] ) );

    }

}
