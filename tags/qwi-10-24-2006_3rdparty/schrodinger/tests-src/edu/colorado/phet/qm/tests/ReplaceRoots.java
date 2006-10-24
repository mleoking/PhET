/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests;

import java.io.*;

/**
 * User: Sam Reid
 * Date: May 12, 2006
 * Time: 11:11:41 PM
 * Copyright (c) May 12, 2006 by Sam Reid
 */

public class ReplaceRoots {
    static String newText = ":ssh:samreid@phet.cvs.sourceforge.net:/cvsroot/phet";

    public static void main( String[] args ) {
        File root = new File( "C:\\PhET\\projects-ii\\balloons" );
        searchAndReplace( root );
    }

    private static void searchAndReplace( File root ) {
        if( root.getName().equalsIgnoreCase( "root" ) ) {
            replace( root );
        }
        if( root.isDirectory() ) {
            for( int i = 0; i < root.listFiles().length; i++ ) {
                searchAndReplace( root.listFiles()[i] );
            }
        }
    }

    private static void replace( File root ) {
        System.out.println( "root.getAbsolutePath() = " + root.getAbsolutePath() );
        try {
//            FileOutputStream fileOutputStream=new FileOutputStream( root);
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( root ) );
            bufferedWriter.write( newText );
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
