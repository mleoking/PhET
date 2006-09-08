/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import javax.swing.*;
import java.io.*;

/**
 * CvsUtil
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class CvsUtil {
    boolean verbose;
    boolean backup;
    private String fileSeparator;

    public CvsUtil( String[] args ) {
        parseArgs( args );
    }

    abstract protected void displayHelp();

    private void parseArgs( String[] args ) {
        if( args.length == 0 ) {
            displayHelp();
            System.exit( 0 );
        }
        for( int i = 0; i < args.length; i++ ) {
            if( args[i].equals( "-v" ) ) {
                verbose = true;
            }
            if( args[i].equals( "-b")) {
                backup = true;
            }
            if( args[i].equals( "-h" )) {
                displayHelp();
            }
        }
    }

    protected void makeBackup( File sourceDir ) throws IOException {
        fileSeparator = System.getProperty( "file.separator" );
        String backupDirName = sourceDir.getName() + "-backup";

        if( verbose ) {
            System.out.println( "=====================================================================" );
            System.out.println( "Backing up " + sourceDir.getAbsolutePath() + " to " + backupDirName );
            System.out.println( "=====================================================================" );
        }
        copyTree( sourceDir, backupDirName );
    }

    private void copyTree( File sourceDir, String backupDirName ) throws IOException {
        File backupDir = new File( backupDirName );
        backupDir.mkdir();
        File[] files = sourceDir.listFiles();
        for( int i = 0; i < files.length; i++ ) {
            File source = files[i];
            if( source.isDirectory()) {
                copyTree( source, new String( backupDirName + fileSeparator + source.getName() ));
            }
            else {
                File copy = new File( backupDirName + fileSeparator + source.getName() );
                copyFile( source, copy );
            }
        }
    }

    private void copyFile( File source, File copy ) throws IOException {
        if( verbose ) {
            System.out.println( "Copying " + source.getAbsolutePath() + " to " + copy.getAbsolutePath() );
        }
        FileInputStream in = new FileInputStream( source );
        FileOutputStream out = new FileOutputStream( copy );

        // Transfer bytes from in to out, from almanac
        byte[] buf = new byte[1024];
        int len;
        while( ( len = in.read( buf ) ) > 0 ) {
            out.write( buf, 0, len );
        }
        out.flush();
        in.close();
        out.close();
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "CVS Util");
        frame.setContentPane( new CvsUtilPanel() );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
