/**
 * Class: StringResourceReader
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Apr 16, 2004
 */
package edu.colorado.phet.coreadditions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringResourceReader {

    public String read( String path ) {
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream( path );
        StringBuffer resourceBuffer = new StringBuffer();
        int i;
        int charsRead = 0;
        final int size = 1024;
        char[] charArray = new char[size];
        BufferedReader resourceReader = new BufferedReader( new InputStreamReader( is ) );
        try {
            while( ( charsRead = resourceReader.read( charArray, 0, size ) ) != -1 ) {
                resourceBuffer.append( charArray, 0, charsRead );
            }

            while( ( i = resourceReader.read() ) != -1 ) {
                resourceBuffer.append( (char)i );
            }
            resourceReader.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return resourceBuffer.toString();
    }
}
