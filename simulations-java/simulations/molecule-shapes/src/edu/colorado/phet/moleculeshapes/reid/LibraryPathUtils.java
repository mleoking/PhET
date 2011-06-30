package edu.colorado.phet.moleculeshapes.reid;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utilities for dynamically adding the native binaries to the java.library.path at runtime, see: see http://nicklothian.com/blog/2008/11/19/modify-javalibrarypath-at-runtime/
 * This code relies on undocumented features, so should be thoroughly tested on a variety of platforms.
 *
 * @author Sam Reid
 */
public class LibraryPathUtils {

    //adds the specified directory to the java.library.path system property
    public static void addDir( String s ) throws IOException {
        try {
            Field field = ClassLoader.class.getDeclaredField( "usr_paths" );
            field.setAccessible( true );
            String[] paths = (String[]) field.get( null );
            for ( int i = 0; i < paths.length; i++ ) {
                if ( s.equals( paths[i] ) ) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy( paths, 0, tmp, 0, paths.length );
            tmp[paths.length] = s;
            field.set( null, tmp );
            System.setProperty( "java.library.path", System.getProperty( "java.library.path" ) + File.pathSeparator + s );
        }
        catch ( IllegalAccessException e ) {
            throw new IOException( "Failed to get permissions to set library path" );
        }
        catch ( NoSuchFieldException e ) {
            throw new IOException( "Failed to get field handle to set library path" );
        }
    }

    //Unzip the zip file to the specified folder
    public static void unzip( File zipFileName, File targetDir ) throws IOException {
        unzip( zipFileName, targetDir, new FileFilter() {
            public boolean accept( File file ) {
                return true;
            }
        } );
    }

    //Copy streams
    public static void copy( InputStream source, OutputStream dest, boolean buffered ) throws IOException {
        //TODO: buffering is disabled until file truncation issue is resolved
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

    //Copy a stream and close it afterwards
    private static void copyAndClose( InputStream source, OutputStream dest, boolean buffered ) throws IOException {
        copy( source, dest, buffered );
        source.close();
        dest.close();
    }

    //Unzip the zip file to the specified location, but only removing files that match the filter
    private static void unzip( File zipFileName, File targetDir, FileFilter filter ) throws IOException {
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

    //Copy the specified source file to the specified destination, overwriting anything that was there before
    public static void copyTo( File source, File dest ) throws IOException {
        dest.getParentFile().mkdirs();
        copyAndClose( new FileInputStream( source ), new FileOutputStream( dest ), true );
    }
}