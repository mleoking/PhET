package edu.colorado.phet.build.patches;

import java.io.*;

/**
 * Created by: Sam
 * Aug 19, 2008 at 2:27:17 PM
 */
public class PatchLanguages {
    private String[] args;
    private File tempDir;
    private int count = 0;
    private int maxCount = Integer.MAX_VALUE;

    public PatchLanguages( String[] args ) {
        this.args = args;
        tempDir = new File( "temp-patches" );
        tempDir.mkdirs();
        if ( args.length > 0 ) {
            maxCount = Integer.parseInt( args[0] );
            System.out.println( "stopping after " + maxCount + " jar files" );
        }
    }

    public static void main( String[] args ) throws IOException, InterruptedException {
        new PatchLanguages( args ).start();
    }

    private void start() throws IOException, InterruptedException {
        File startDir = new File( "." );
        visitDirectory( startDir );
    }

    private void visitDirectory( File dir ) throws IOException, InterruptedException {
        File[] f = dir.listFiles();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            if ( file.isDirectory() ) {
                visitDirectory( file );
            }
            else {
                visitFile( file );
            }
        }
    }

    private void visitFile( File file ) throws IOException, InterruptedException {
        if ( file.getName().toLowerCase().endsWith( ".jar" ) ) {
            visitJarFile( file );
        }
    }

    private void visitJarFile( File file ) throws IOException, InterruptedException {
        System.out.println( "Found JAR file: " + file.getAbsolutePath() );
        File dir = new File( tempDir, file.getName() + ".tmp" );
        dir.mkdirs();
        Process extractProcess = Runtime.getRuntime().exec( "jar xf " + file.getAbsolutePath(), new String[0], dir );
        extractProcess.waitFor();

        patchTranslationFilesDir( dir );

        Process compressProcess = Runtime.getRuntime().exec( "jar cfM " + file.getAbsolutePath() + " .", new String[0], dir );

        compressProcess.waitFor();
        count++;
        if ( count >= maxCount ) {
            System.exit( 0 );
        }
//        System.exit( 0 );
    }

    private void patchTranslationFilesDir( File dir ) throws IOException {
        File[] f = dir.listFiles();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            if ( file.isDirectory() ) {
                patchTranslationFilesDir( file );
            }
            else {
                String suffix = "-strings.properties";
                String path = file.getAbsolutePath();
                if ( path.toLowerCase().endsWith( suffix ) ) {
                    String newFilename = path.substring( 0, path.length() - suffix.length() ) + "-strings_en.properties";
                    File dest = new File( newFilename );
                    copyTo( file, dest );
                    System.out.println( "\tCopied from: " + file.getAbsolutePath() + " to " + dest.getAbsolutePath() );
                }
            }
        }
    }

    public static void copyTo( File source, File dest ) throws IOException {
        copyAndClose( new FileInputStream( source ), new FileOutputStream( dest ) );
    }

    public static void copy( InputStream source, OutputStream dest ) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ( ( bytesRead = source.read( buffer ) ) >= 0 ) {
            dest.write( buffer, 0, bytesRead );
        }
    }

    public static void copyAndClose( InputStream source, OutputStream dest ) throws IOException {
        copy( source, dest );
        source.close();
        dest.close();
    }
}
