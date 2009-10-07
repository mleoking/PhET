package edu.colorado.phet.translationutility.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Test whether Class.getProtectionDomain returns a properly encoded URL.
 * It doesn't seem to handle encoding of '+' properly, which should be encoded as "%2B".
 * See Unfuddle #1817.
 * 
 * Correctly encoded/decoded output:
 * url=file:/User/cmalley/Downloads/foo%2Bbar/
 * file=/User/cmalley/Downloads/foo+bar/
 * 
 * Typical bad output:
 * url=file:/User/cmalley/Downloads/foo+bar/
 * file=/User/cmalley/Downloads/foo bar/
 * 
 * This tested broken on Mac OS 10.5, Vista.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestURLDecoder {
    public static void main( String[] args ) throws IOException {
        URL url = TestURLDecoder.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println( "url=" + url );
        File file = new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );
        System.out.println( "file=" + file.getAbsolutePath() );
    }
}
