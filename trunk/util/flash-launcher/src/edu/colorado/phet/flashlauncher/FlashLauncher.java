package edu.colorado.phet.flashlauncher;

import java.io.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.*;

import edu.colorado.phet.flashlauncher.util.BareBonesBrowserLaunch;

/**
 * Created by: Sam
 * May 29, 2008 at 7:53:28 AM
 */
public class FlashLauncher {
    private String[] args;
    private String sim;
    private String language;

    public FlashLauncher( String[] args ) throws IOException {
        this.args = args;
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "args.txt" );
        BufferedReader bu = new BufferedReader( new InputStreamReader( inputStream ) );
        String line = bu.readLine();
        StringTokenizer stringTokenizer = new StringTokenizer( line, " " );
        System.out.println( "line = " + line );
        this.sim = stringTokenizer.nextToken();
        this.language = stringTokenizer.nextToken();
    }

    public static void main( String[] args ) throws IOException {
//        JOptionPane.showMessageDialog( null, System.getProperty( "java.class.path" ) );
        new FlashLauncher( args ).start();
    }

    private void start() throws IOException {
        System.out.println( "FlashLauncher.start" );
        System.out.println( "System.getProperty( \"user.dir\" ) = " + System.getProperty( "user.dir" ) );
        File currentDir = new File( System.getProperty( "user.dir" ) );
        File tempDir = new File( currentDir, "flash-launcher-temp" );
        String x = System.getProperty( "java.class.path" );
        File f = new File( x );
        System.out.println( "x = " + x );
        unzip( new File( currentDir, f.getName() ), tempDir );
        BareBonesBrowserLaunch.openURL( new File( tempDir, sim + "_" + language + ".html" ).getAbsolutePath() );
    }


    //todo: the following utility functions were copied from FileUtils so that we didn't have to
    //todo: gather all lib dependencies from FileUtils
    public static void unzip( File zipFileName, File targetDir ) throws IOException {
        unzip( zipFileName, targetDir, new FileFilter() {
            public boolean accept( File file ) {
                return true;
            }
        } );
    }

    public static void unzip( File zipFileName, File targetDir, FileFilter filter ) throws IOException {
        ZipFile zipFile = new ZipFile( zipFileName );

        Enumeration enumeration = zipFile.entries();

        while ( enumeration.hasMoreElements() ) {
            ZipEntry entry = (ZipEntry) enumeration.nextElement();

            String name = entry.getName();

            if ( filter.accept( new File( targetDir, name ) ) ) {
                if ( entry.isDirectory() ) {
                    new File( targetDir, name ).mkdirs();
                }
                else {
                    File targetFile = new File( targetDir, name );

                    targetFile.getParentFile().mkdirs();

                    InputStream source = zipFile.getInputStream( entry );
                    FileOutputStream fileOutputStream = new FileOutputStream( targetFile );

                    copyAndClose( source, fileOutputStream, false );
                }
            }
        }
        zipFile.close();
    }

    public static void copyTo( File source, File dest ) throws IOException {
        copyAndClose( new FileInputStream( source ), new FileOutputStream( dest ), true );
    }

    public static void copy( InputStream source, OutputStream dest, boolean buffered ) throws IOException {
        //todo: buffering is disabled until file truncation issue is resolved
        buffered = false;
        if ( buffered ) {
            source = new BufferedInputStream( source );
            dest = new BufferedOutputStream( dest );
        }

        int bytesRead;

        byte[] buffer = new byte[1024];

        while ( ( bytesRead = source.read( buffer ) ) >= 0 ) {
            dest.write( buffer, 0, bytesRead );
        }
    }

    public static void copyAndClose( InputStream source, OutputStream dest, boolean buffered ) throws IOException {
        copy( source, dest, buffered );
        source.close();
        dest.close();
    }

}
