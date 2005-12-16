package edu.colorado.phet.qm.util;

import java.io.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 9, 2005
 * Time: 1:48:01 PM
 * Copyright (c) Dec 9, 2005 by Sam Reid
 */

public class QueryReplace {
    public static void main( String[] args ) throws IOException {
        new QueryReplace().start();
    }

    private void start() throws IOException {
        File start = new File( "C:\\PhET\\projects-ii\\schrodinger\\phetcommon\\src" );
        queryReplace( start );
    }

    private void queryReplace( File f ) throws IOException {
        System.out.println( "looking in f=" + f );
        if( f.isDirectory() ) {
            File[]files = f.listFiles( new FileFilter() {
                public boolean accept( File pathname ) {
                    return ( pathname.isFile() && pathname.getAbsolutePath().toLowerCase().endsWith( ".java" ) ) || pathname.isDirectory();
                }
            } );
            for( int i = 0; i < files.length; i++ ) {
                File file = files[i];
                queryReplace( file );
            }
        }
        else {
            if( f.getAbsolutePath().toLowerCase().endsWith( ".java" ) ) {
                replaceText( f );
            }
        }
    }

    private void replaceText( File f ) throws IOException {
        String[] string = toString( f );
        if( string[0].equals( "/* Copyright 2004, Sam Reid */" ) ) {
            String replace = "/* Copyright 2003-2005, University of Colorado */\n" +
                             "\n" +
                             "/*\n" +
                             " * CVS Info -\n" +
                             " * Filename : $Source$\n" +
                             " * Branch : $Name$\n" +
                             " * Modified by : $Author$\n" +
                             " * Revision : $Revision$\n" +
                             " * Date modified : $Date$\n" +
                             " */";
            string[0] = replace;
            writeFile( f, string );
            System.out.println( "f.getAbsolutePath() = " + f.getAbsolutePath() );
        }
    }

    private void writeFile( File f, String[] string ) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( f ) );
        for( int i = 0; i < string.length; i++ ) {
            String s = string[i];
            bufferedWriter.write( s );
            if( i != string.length - 1 ) {
                bufferedWriter.newLine();
            }
        }
        bufferedWriter.close();
    }

    private String[] toString( File f ) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new FileReader( f ) );
        ArrayList text = new ArrayList();
        for( String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine() ) {
            text.add( s );
        }
        bufferedReader.close();
        return (String[])text.toArray( new String[0] );
    }
}
