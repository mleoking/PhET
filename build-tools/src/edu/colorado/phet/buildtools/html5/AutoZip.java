package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.taskdefs.Zip;

/**
 * For testing with CocoonJS, scan a filesystem and automatically re-zip whenever anything changes.
 * I would have preferred to do this with SBT but couldn't figure out an easy way to do so.
 * <p/>
 * Note: this deletes files from your filesystem, so please be sure you know what you are doing before launching this.
 *
 * @author Sam Reid
 */
public class AutoZip {
    static long lastModified = 0;

    public static void main( String[] args ) {
        final File root = new File( args[0] );
        System.out.println( "root = " + root );

        new Thread( new Runnable() {
            public void run() {
                long count = 0;
                while ( true ) {
                    long modifiedTime = lastModified( root );
                    if ( modifiedTime > lastModified ) {
                        lastModified = modifiedTime;
                        try {
                            runTask( root );
                        }
                        catch ( Exception e1 ) {
                            e1.printStackTrace();
                            lastModified = 0;
                        }
                        System.out.println();
                        System.out.println( "Finished task" );
                    }
                    else {
                        if ( count % 30 == 0 ) {
                            System.out.print( "." );
                        }
                    }
                    count++;
                    try {
                        Thread.sleep( 30 );
                    }
                    catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    private static void runTask( final File sourceRoot ) throws IOException {
        final File destination = new File( sourceRoot.getParentFile(), "autozip.zip" );

        deleteOldZipAndCreateNewZip( sourceRoot, destination );
    }

    public static void deleteOldZipAndCreateNewZip( final File sourceRoot, final File destination ) {
        if ( destination.exists() ) {
            boolean deleted = destination.delete();
            if ( !deleted ) {
                throw new RuntimeException( "errored" );
            }
        }
        zip( sourceRoot, destination );
    }

    private static void zip( final File root, final File autozip ) {
        Zip zip = new Zip();
        zip.setBasedir( root );
        zip.setDestFile( autozip );
        zip.setProject( new Project() );
        zip.setLocation( Location.UNKNOWN_LOCATION );
        zip.setOwningTarget( new Target() );
        zip.init();
        zip.execute();
    }

    private static long lastModified( final File root ) {
        if ( root.isDirectory() ) {
            long base = root.lastModified();
            for ( File file : root.listFiles() ) {
                final long last = file.lastModified();
                if ( last > base ) {
                    base = last;
                }
            }
            return base;
        }
        else {
            return root.lastModified();
        }
    }
}