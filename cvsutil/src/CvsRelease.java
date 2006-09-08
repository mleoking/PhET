/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import java.io.File;
import java.io.IOException;

/**
 * CvsRelease
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CvsRelease extends CvsUtil {
    private File rootDir;

    public CvsRelease( String[] args ) throws IOException {
        super( args );
        parseArgs( args );
        if( backup ) {
            makeBackup( rootDir );
        }

        if( verbose ) {
            System.out.println( "=========================================" );
            System.out.println( "Releasing " + rootDir.getAbsolutePath() );
            System.out.println( "=========================================" );
        }
        processDir( rootDir );
    }

    protected void parseArgs( String[] args ) {
        if( args.length < 1 ) {
            System.exit( 0 );
        }
        rootDir = new File( args[0] );
    }

    protected void displayHelp() {
        System.out.println( "Useage:" );
        System.out.println( "java -jar cvsrelease.jar root-directory [-v][-b]" );
        System.out.println( "-v\tverbose" );
        System.out.println( "-b\tmake backup first" );
    }

    void processDir( File file ) {
        if( verbose ) {
            System.out.println( "directory = " + file.getPath() );
        }
        if( file.isDirectory() ) {
            if( file.getName().endsWith( "CVS" ) ) {
                deleteContents( file );
                file.delete();
            }
            else {
                File[] files = file.listFiles();
                for( int i = 0; i < files.length; i++ ) {
                    File file1 = files[i];
                    if( file1.isDirectory() ) {
                        processDir( file1 );
                    }
                }
            }
        }
    }

    void deleteContents( File file ) {
        File[] files = file.listFiles();
        for( int i = 0; i < files.length; i++ ) {
            File file1 = files[i];
            if( verbose ) {
                System.out.println( "file = " + files[i].getPath() );
            }
            file1.delete();
        }
    }


    public static void main( String[] args ) {
        try {
            new CvsRelease( args );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
