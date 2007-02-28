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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;

/**
 * CvsMirgrate
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CvsMigrate extends CvsUtil {
    private File rootDir;
    private String newRootString;

    public CvsMigrate( String[] args ) throws IOException {
        super( args );
        parseArgs( args );
        if( backup ) {
            makeBackup( rootDir );
        }

        if( verbose ) {
            System.out.println( "==================================================================" );
            System.out.println( "Migrating " +  rootDir.getAbsolutePath() + " to " + newRootString );
            System.out.println( "==================================================================" );
        }
        processDir( rootDir );
    }

    protected void parseArgs( String[] args ) {
        if( args.length < 2 ) {
            System.out.println( "Insufficient arguments" );
            System.exit( 0 );
        }
        rootDir = new File( args[0] );
        newRootString = args[1];
    }

    protected void displayHelp() {
        System.out.println( "Useage:" );
        System.out.println( "java -jar cvsmigrate.jar source-root-directory new-CVS-repository-root [-v][-b]" );
        System.out.println( "-v\tverbose" );
        System.out.println( "-b\tmake backup first" );
    }

    void processDir( File file ) {
        if( verbose ) {
            System.out.println( "Processing directory: " + file.getPath() );
        }
        if( file.isDirectory() ) {
            if( file.getName().endsWith( "CVS" ) ) {
                setNewRoot( file );
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

    void setNewRoot( File rootFileDir ) {
        File[] files = rootFileDir.listFiles();
        for( int i = 0; i < files.length; i++ ) {
            File file1 = files[i];
            if( file1.getName().equals( "Root" ) ) {
                String rootFilePath = file1.getAbsolutePath();
                file1.delete();
                try {
                    BufferedWriter newRootFile = new BufferedWriter( new FileWriter( rootFilePath ) );
                    newRootFile.write( newRootString );
                    newRootFile.close();
                }
                catch( IOException e ) {
                }
            }
        }
    }


    public static void main( String[] args ) {
        try {
            new CvsMigrate( args);
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
