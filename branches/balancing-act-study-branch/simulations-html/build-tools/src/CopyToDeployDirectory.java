package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

//Simple build script for copying dependencies to the right relative locations.
public class CopyToDeployDirectory {
    public static final String OUTPUT_DIR_NAME = "deploy-output";

    public static void main( String[] args ) throws IOException {
        File simDir = new File( args[0] );
        File root = simDir.getParentFile();
        File destination = new File( simDir, OUTPUT_DIR_NAME );
        File buildFile = new File( simDir, "build.txt" );

        String buildFileText = FileUtils.loadFileAsString( buildFile );

        StringTokenizer st = new StringTokenizer( buildFileText, "\n" );
        ArrayList<ArrayList<String>> commands = new ArrayList<ArrayList<String>>();
        while ( st.hasMoreTokens() ) {
            String token = st.nextToken().trim();
            if ( token.length() > 0 && !token.startsWith( "#" ) ) {
//                System.out.println( "Line: " + token );
                StringTokenizer elements = new StringTokenizer( token, " " );
                ArrayList<String> entry = new ArrayList<String>();
                while ( elements.hasMoreTokens() ) {
                    entry.add( elements.nextToken() );
                }
                commands.add( entry );
            }
        }

        for ( ArrayList<String> command : commands ) {
            System.out.println( "command = " + command );

            //Right now copy the contents to the destination
            copyRecursive( new File( root, command.get( 0 ) ), new File( destination, command.get( 0 ) ) );
        }

        copyRecursive( simDir, new File( destination, simDir.getName() ) );
    }

    public static void copyRecursive( File src, File dest ) throws IOException {
        if ( src.isDirectory() ) {
            //never copy .svn metadata
            if ( !src.getName().equals( ".svn" ) &&
                 !src.getName().equals( ".git" ) &&
                 !src.getName().equals( "node-modules" ) &&
                 !src.getName().equals( "node_modules" ) &&
                 !src.getName().equals( OUTPUT_DIR_NAME ) &&
                 !src.getName().startsWith( "deploy-output" ) ) {
                dest.mkdirs();
                File[] f = src.listFiles();
                for ( int i = 0; i < f.length; i++ ) {
                    File child = f[i];
                    copyRecursive( child, new File( dest, child.getName() ) );
                }
            }
        }
        else {
            FileUtils.copyTo( src, dest );
        }
    }
}