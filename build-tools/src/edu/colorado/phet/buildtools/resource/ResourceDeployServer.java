package edu.colorado.phet.buildtools.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.JARGenerator;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.PhetJarSigner;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;

/**
 * Builds all files necessary to test and run simulations with the new resource before they are published (copied into
 * the live sim directories)
 * <p/>
 * See ResourceDeployClient for the main documentation.
 * <p/>
 * NOTE: run on the server, so do not rename / move this without changing the other references under
 * edu.colorado.phet.buildtools.resource
 */
public class ResourceDeployServer implements IProguardKeepClass {

    private String jarCommand;
    private File buildLocalProperties;
    private File resourceDir;

    private File resourceFile;
    private String resourceDestination;
    private String[] sims;
    private File backupDir;
    private String mode;
    private boolean onlyAllJARs;
    private boolean generateJARs;
    private boolean copySWFs;
    private boolean copyJNLPs;
    private File testDir;

    public ResourceDeployServer( String jarCommand, File buildLocalProperties, File resourceDir ) {
        this.jarCommand = jarCommand;
        this.buildLocalProperties = buildLocalProperties;
        this.resourceDir = resourceDir;

        BuildLocalProperties.initFromPropertiesFile( buildLocalProperties );

        File propertiesFile = ResourceDeployUtils.getResourceProperties( resourceDir );
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
        resourceFile = new File( ResourceDeployUtils.getResourceSubDir( resourceDir ), resourceFilename );

        if ( !resourceFile.exists() ) {
            System.out.println( "Cannot locate resource file, aborting" );
            return;
        }

        resourceDestination = properties.getProperty( "resourceDestination" );
        if ( resourceDestination.startsWith( "/" ) ) {
            resourceDestination = resourceDestination.substring( 1 );
        }

        mode = properties.getProperty( "mode" );
        if ( mode.equals( "java" ) ) {
            onlyAllJARs = true;
            generateJARs = true;
            copySWFs = false;
            copyJNLPs = true;
        }
        else if ( mode.equals( "flash" ) ) {
            onlyAllJARs = false;
            generateJARs = false;
            copySWFs = true;
            copyJNLPs = false;
        }

        // unused, replaced with mode (see above)
        //onlyAllJARs = properties.getProperty( "onlyAllJARs" ).equals( "true" );
        //generateJARs = properties.getProperty( "generateJARs" ).equals( "true" );

        String simsString = properties.getProperty( "sims" );
        sims = simsString.split( "," );

        backupDir = new File( resourceDir, "backup" );
        backupDir.mkdir();

        try {
            System.out.println( "*** Creating backup JARs" );
            createBackupJARs();

            System.out.println( "*** Copying test JARs" );
            copyTestJARs();

            System.out.println( "*** Poking JARs" );
            pokeJARs();

            System.out.println( "*** Signing JARs" );
            signJARs();

            if ( generateJARs ) {
                System.out.println( "*** Generating offline JARs" );
                generateOfflineJARs();
            }

            System.out.println( "*** Backing up extra files" );
            backupExtras();

            System.out.println( "*** Copying extra files" );
            copyExtras();

            if ( copySWFs ) {
                copyFlashSWFs();
            }

            if ( copyJNLPs ) {
                copyJavaJNLPs();
            }
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

    private void copyJavaJNLPs() throws IOException {
        testDir = ResourceDeployUtils.getTestDir( resourceDir );

        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];

            File backupSimDir = new File( backupDir, sim );
            File testSimDir = new File( testDir, sim );
            File simDir = new File( getLiveSimsDir(), sim );

            File[] jarFiles = backupSimDir.listFiles( new FilenameFilter() {
                public boolean accept( File file, String s ) {
                    return s.endsWith( ".jar" ) && !s.endsWith( "_all.jar" );
                }
            } );

            for ( File jarFile : jarFiles ) {
                String jnlpName = jarFile.getName().replace( ".jar", ".jnlp" );
                File baseJnlpFile = new File( simDir, jnlpName );
                if ( !baseJnlpFile.exists() ) {
                    try {
                        System.out.println( "WARNING: JNLP does not exist: " + baseJnlpFile.getCanonicalPath() );
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                    finally {
                        continue;
                    }
                }
                FileUtils.copyToDir( baseJnlpFile, backupSimDir );
                FileUtils.copyToDir( baseJnlpFile, testSimDir );
            }

        }
    }

    private void createBackupJARs() throws IOException {
        File liveDir = getLiveSimsDir();
        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];

            File simDir = new File( liveDir, sim );

            if ( !simDir.exists() ) {
                System.out.println( "WARNING: skipping sim dir " + simDir.getCanonicalPath() + ", does not exist" );
                continue;
            }

            File[] jarFiles = simDir.listFiles( new FilenameFilter() {
                public boolean accept( File file, String name ) {
                    return name.endsWith( ".jar" );
                }
            } );

            if ( jarFiles.length == 0 ) {
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
        testDir = ResourceDeployUtils.getTestDir( resourceDir );
        testDir.mkdir();

        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];

            File backupSimDir = new File( backupDir, sim );
            File testSimDir = new File( testDir, sim );
            testSimDir.mkdir();

            File[] jarFiles = backupSimDir.listFiles();

            for ( int j = 0; j < jarFiles.length; j++ ) {
                File jarFile = jarFiles[j];

                if ( onlyAllJARs && !jarFile.getName().endsWith( "_all.jar" ) ) {
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
        PhetJarSigner phetJarSigner = new PhetJarSigner( BuildLocalProperties.getInstance() );
        phetJarSigner.signJar( jarFile );
    }

    private void generateOfflineJARs() throws IOException, InterruptedException {
        JARGenerator generator = new JARGenerator();
        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];

            File testSimDir = new File( testDir, sim );
            File[] jarFiles = testSimDir.listFiles();

            for ( int j = 0; j < jarFiles.length; j++ ) {
                File jarFile = jarFiles[j];

                generator.generateOfflineJARs( jarFile, jarCommand, BuildLocalProperties.getInstance() );
            }
        }
    }

    private void backupExtras() throws IOException {
        File liveDir = getLiveSimsDir();
        File extrasDir = ResourceDeployUtils.getExtrasDir( resourceDir );
        if ( !extrasDir.exists() ) {
            return;
        }

        File[] simExtraDirs = extrasDir.listFiles();

        for ( int i = 0; i < simExtraDirs.length; i++ ) {
            File simExtraDir = simExtraDirs[i];

            String sim = simExtraDir.getName();

            File[] extraFiles = simExtraDir.listFiles();

            for ( int j = 0; j < extraFiles.length; j++ ) {
                File extraFile = extraFiles[j];

                File liveExtraFile = new File( liveDir, sim + "/" + extraFile.getName() );
                if ( liveExtraFile.exists() ) {
                    File backupExtraDir = new File( backupDir, sim );
                    backupExtraDir.mkdirs();

                    System.out.println( "Copying live extra file " + liveExtraFile.getCanonicalPath() + " to " + backupExtraDir.getCanonicalPath() );
                    FileUtils.copyToDir( liveExtraFile, backupExtraDir );
                }
                else {
                    System.out.println( "* WARNING: Live extra file does not exist: " + liveExtraFile.getCanonicalPath() );
                }
            }
        }
    }

    private void copyExtras() throws IOException {
        File extrasDir = ResourceDeployUtils.getExtrasDir( resourceDir );
        if ( !extrasDir.exists() ) {
            return;
        }

        File[] simExtraDirs = extrasDir.listFiles();

        for ( int i = 0; i < simExtraDirs.length; i++ ) {
            File simExtraDir = simExtraDirs[i];

            String sim = simExtraDir.getName();

            File[] extraFiles = simExtraDir.listFiles();

            for ( int j = 0; j < extraFiles.length; j++ ) {
                File extraFile = extraFiles[j];

                File testExtraDir = new File( testDir, sim );
                testExtraDir.mkdirs();

                FileUtils.copyToDir( extraFile, testExtraDir );
            }
        }
    }

    private void copyFlashSWFs() throws IOException {
        File[] testSimDirs = testDir.listFiles();

        for ( int i = 0; i < testSimDirs.length; i++ ) {
            File testSimDir = testSimDirs[i];
            String sim = testSimDir.getName();
            File backupSimDir = new File( backupDir, sim );
            backupSimDir.mkdirs();
            File liveSimDir = new File( ResourceDeployUtils.getLiveSimsDir( resourceDir ), sim );

            File swfFile = new File( liveSimDir, sim + ".swf" );

            if ( !swfFile.exists() ) {
                System.out.println( "WARNING: sim SWF doesn't exist: " + sim + "/" + swfFile.getName() + " at expected location " + swfFile.getAbsolutePath() );
                continue;
            }

            // copy the SWF to the test dir so that we can test the new generated HTMLs
            FileUtils.copyToDir( swfFile, testSimDir );

            // copy the SWF to the backup sim dir so that we will have them for posterity (and a warning won't be seen
            // if the user reverts)
            FileUtils.copyToDir( swfFile, backupSimDir );
        }
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
