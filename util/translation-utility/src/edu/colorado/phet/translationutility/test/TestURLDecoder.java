package edu.colorado.phet.translationutility.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Demonstrates a problem with encoding of '+' character.
 */
public class TestURLDecoder {
    public static void main( String[] args ) throws IOException {

        // Filename contains a '+ character.
        String filename = "/tmp/foo+bar";
        System.out.println( "filename=" + filename );

        // Encoding should be "/tmp/foo%2Bbar" but is "/tmp/foo+bar".
        URL url = new URL( "file:" + filename );
        System.out.println( "url=" + url );

        // Decoding is "/tmp/foo bar" instead of the original filename.
        File file = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
        System.out.println( "file=" + file.getAbsolutePath() );
    }
}
