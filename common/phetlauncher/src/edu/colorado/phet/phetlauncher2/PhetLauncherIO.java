/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import java.io.*;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 10:49:03 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class PhetLauncherIO {
    public static String readString( File file ) throws IOException {
        String str = "";
        BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
        for( String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
            str += line + System.getProperty( "line.separator" );
        }
        bufferedReader.close();
        return str.substring( 0, str.length() - 1 );
    }

    public static void writeString( String str, File file ) throws IOException {
        file.getParentFile().mkdirs();
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( file ) );
        bufferedWriter.write( str );
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
