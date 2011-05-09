/* Copyright 2007, University of Colorado */
package edu.colorado.phet.buildtools.util;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    private static String DEFAULT_ENCODING = "utf-8";

    private static final Logger logger = Logger.getLogger( FileUtils.class.getName() );

    public static File getTmpDir() {
        File file = new File( System.getProperty( "java.io.tmpdir" ) );
        file.mkdirs();
        return file;
    }

    public static String loadFileAsString( File file ) throws IOException {
        return loadFileAsString( file, DEFAULT_ENCODING );
    }

    public static String loadFileAsString( File file, String encoding ) throws IOException {
        InputStream inStream = new FileInputStream( file );

        ByteArrayOutputStream outStream;

        try {
            outStream = new ByteArrayOutputStream();

            int c;
            while ( ( c = inStream.read() ) >= 0 ) {
                outStream.write( c );
            }
            outStream.flush();
        }
        finally {
            inStream.close();
        }

        return new String( outStream.toByteArray(), encoding );
    }


    public static void filter( File inputFile, File outputFile, HashMap map ) throws IOException {
        filter( inputFile, outputFile, map, DEFAULT_ENCODING );
    }

    public static void writeString( File outputFile, String text, String encoding ) throws IOException {
        writeBytes( outputFile, text.getBytes( encoding ) );
    }

    public static void writeString( File outputFile, String text ) throws IOException {
        writeString( outputFile, text, DEFAULT_ENCODING );
    }

    public static void writeBytes( File outputFile, byte[] bytes ) throws IOException {
        outputFile.getParentFile().mkdirs();
        FileOutputStream outputStream = new FileOutputStream( outputFile );
        try {
            outputStream.write( bytes );
        }
        finally {
            outputStream.close();
        }
    }

    public static String replaceFirst( String body, String find, String replacement ) {
        int indexOfFindText = body.indexOf( find );

        if ( indexOfFindText != -1 ) {

            String before = body.substring( 0, indexOfFindText );
            String after = body.substring( indexOfFindText + find.length() );

            body = before + replacement + after;
        }

        return body;
    }

    public static String replaceAll( String body, String find, String replacement ) {
        boolean changed;

        do {
            changed = false;

            int indexOfFindText = body.indexOf( find );

            if ( indexOfFindText != -1 ) {
                changed = true;

                String before = body.substring( 0, indexOfFindText );
                String after = body.substring( indexOfFindText + find.length() );

                body = before + replacement + after;
            }

        }
        while ( changed );

        return body;
    }

    public static String filter( HashMap map, String file ) {
        Set set = map.keySet();
        for ( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String value = (String) map.get( key );

            file = replaceAll( file, "@" + key + "@", value );
        }
        return file;
    }

    public static void filter( File source, File destFile, HashMap filterMap, String encoding ) throws IOException {
        String text = loadFileAsString( source, encoding );
        String result = filter( filterMap, text );
        writeString( destFile, result, encoding );
    }

    public static void delete( File file ) {
        delete( file, false );
    }

    public static void delete( File file, boolean verbose ) {
        if ( file.isDirectory() ) {
            File[] children = file.listFiles();

            for ( int i = 0; i < children.length; i++ ) {
                delete( children[i], verbose );
            }
        }
        if ( verbose ) {
            System.out.println( "Deleting: " + file.getAbsolutePath() );
        }
        file.delete();
    }

    public static void copyTo( File source, File dest ) throws IOException {
        dest.getParentFile().mkdirs();
        copyAndClose( new FileInputStream( source ), new FileOutputStream( dest ), true );
    }

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

    public static void copyAndClose( InputStream source, OutputStream dest, boolean buffered ) throws IOException {
        copy( source, dest, buffered );
        source.close();
        dest.close();
    }

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

    public static void jarSingleFile( File rootDir, File file, ZipOutputStream zipOutputStream ) throws IOException {
        if ( file.isDirectory() ) {
            File[] f = file.listFiles();
            for ( int i = 0; i < f.length; i++ ) {
                if ( !f[i].getAbsolutePath().endsWith( "MANIFEST.MF" ) ) {
                    jarSingleFile( rootDir, f[i], zipOutputStream );
                }
            }
        }
        else {
            String path = file.getAbsolutePath().substring( rootDir.getAbsolutePath().length() );
            path = path.replace( '\\', '/' );
            while ( path.startsWith( "/" ) ) {
                path = path.substring( 1 );
            }
            JarEntry zipEntry = new JarEntry( path );
            zipOutputStream.putNextEntry( zipEntry );

            FileInputStream inputStream = new FileInputStream( file );
            copy( inputStream, zipOutputStream, false );
            inputStream.close();

            zipOutputStream.closeEntry();

        }
    }

    public static void zip( File[] filenames, File dest ) {
        // These are the files to include in the ZIP file

        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream( new FileOutputStream( dest ) );

            // Compress the files
            for ( int i = 0; i < filenames.length; i++ ) {
                FileInputStream in = new FileInputStream( filenames[i] );

                // Add ZIP entry to output stream.
                out.putNextEntry( new ZipEntry( filenames[i].getName() ) );//flattens packages

                // Transfer bytes from the file to the ZIP file
                int len;
                while ( ( len = in.read( buf ) ) > 0 ) {
                    out.write( buf, 0, len );
                }

                // Complete the entry
                out.closeEntry();
                in.close();
            }

            // Complete the ZIP file
            out.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

    }

    public static void jar( File dir, File dest ) throws IOException {
        JarOutputStream jarOutputStream = new JarOutputStream( new FileOutputStream( dest ), new Manifest( new FileInputStream( new File( dir, "META-INF/MANIFEST.MF" ) ) ) );
        jarSingleFile( dir, dir, jarOutputStream );
        jarOutputStream.close();
    }

    //http://www.exampledepot.com/egs/java.util.regex/Escape.html
    public static String quote( String name ) {
        if ( name.toLowerCase().indexOf( "\\e" ) > 0 ) {
            throw new RuntimeException( "Quote method will fail" );
        }
        return "\\Q" + name + "\\E";
    }

    public static void main( String[] args ) throws IOException {
        testUnzip();
    }

    public static void testUnzip() throws IOException {
        final Pattern excludePattern = Pattern.compile( quote( "quantum-tunneling" ) + "[\\\\/]localization[\\\\/]" + quote( "quantum-tunneling" ) + ".*\\.properties" );

        unzip( new File( "C:\\Users\\Sam\\AppData\\Local\\Temp\\soluble-salts40662.jar" ), new File( "C:\\Users\\Sam\\AppData\\Local\\Temp\\soluble-salts40662-test-unzip-8" ), new FileFilter() {
            public boolean accept( File file ) {
                return !excludePattern.matcher( file.getAbsolutePath() ).find();
            }
        } );
    }

    public static void addPrefix( File file, String prefix ) throws IOException {
        writeString( file, prefix + loadFileAsString( file ) );
    }

    public static void download( String urlAddress, File file ) throws FileNotFoundException {
        file.getParentFile().mkdirs();
        try {
            OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( file ) );
            InputStream inputStream = new URL( urlAddress ).openConnection().getInputStream();
            byte[] data = new byte[2048];
            int read = 0;
            while ( ( read = inputStream.read( data ) ) != -1 ) {
                outputStream.write( data, 0, read );
            }
            inputStream.close();
            outputStream.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void copyRecursive( File src, File dest ) throws IOException {
        if ( src.isDirectory() ) {
            //never copy .svn metadata
            if ( !src.getName().equals( ".svn" ) ) {
                dest.mkdirs();
                logger.fine( "Created: " + dest.getAbsolutePath() );
                File[] f = src.listFiles();
                for ( int i = 0; i < f.length; i++ ) {
                    File child = f[i];
                    copyRecursive( child, new File( dest, child.getName() ) );
                }
            }
        }
        else {
            copyTo( src, dest );
            logger.fine( "Copied to: " + dest.getAbsolutePath() );
        }
    }

    public static void copyToDir( File src, File dstDir ) throws IOException {
        copyTo( src, new File( dstDir, src.getName() ) );
    }
}
