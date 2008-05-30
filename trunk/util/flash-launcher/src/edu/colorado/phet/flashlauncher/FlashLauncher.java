package edu.colorado.phet.flashlauncher;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
    private String sim;
    private String language;
    private static JTextArea jTextArea;

    public FlashLauncher() throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "args.txt" );
        BufferedReader bu = new BufferedReader( new InputStreamReader( inputStream ) );
        String line = bu.readLine();
        StringTokenizer stringTokenizer = new StringTokenizer( line, " " );
        println( "line = " + line );
        this.sim = stringTokenizer.nextToken();
        this.language = stringTokenizer.nextToken();
        if ( stringTokenizer.hasMoreTokens() && stringTokenizer.nextToken().equals( "-dev" ) ) {
            println( "FlashLauncher.FlashLauncher dev" );
            JFrame frame = new JFrame( "Text" );
            jTextArea = new JTextArea( 10, 50 );
            frame.setContentPane( new JScrollPane( jTextArea ) );
            frame.setVisible( true );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.setSize( 800, 600 );
        }
    }

    public static void println( String string ) {
        System.out.println( string );
        if ( jTextArea != null ) {
            jTextArea.append( string + "\n" );
        }
    }

    public static void main( String[] args ) throws IOException {
//        JOptionPane.showMessageDialog( null, System.getProperty( "java.class.path" ) );
        new FlashLauncher().start();
    }

    private void start() throws IOException {
        println( "FlashLauncher.start" );
        println( "System.getProperty( \"user.dir\" ) = " + System.getProperty( "user.dir" ) );
        File currentDir = new File( System.getProperty( "user.dir" ) );
        File tempDir = new File( currentDir, "temp_phet-flash" );
        File jarfile = getJARFile();
        println( "jarfile = " + jarfile );
        println( "Starting unzip jarfile=" + jarfile + ", tempDir=" + tempDir );
        unzip( jarfile, tempDir );
        println( "Finished unzip" );

        println( "Starting openurl" );
        BareBonesBrowserLaunch.openURL( "file://" + new File( tempDir, sim + "_" + language + ".html" ).getAbsolutePath() );
    }

    private File getJARFile() {
        URL url = FlashLauncher.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            URI uri = new URI( url.toString() );
            return new File( uri.getPath() );
        }
        catch( URISyntaxException e ) {
            println( e.getMessage() );
            e.printStackTrace();
            throw new RuntimeException( e );
        }
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
