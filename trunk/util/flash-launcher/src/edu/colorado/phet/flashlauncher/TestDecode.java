package edu.colorado.phet.flashlauncher;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URI;
import java.net.URL;

/**
 * Created by: Sam
 * May 29, 2008 at 1:10:28 PM
 */
public class TestDecode {
    public static void main( String[] args ) throws UnsupportedEncodingException {
        String fileName = "C:/users/sam%20reid";
        File file = new File( fileName );
        fileName=URLDecoder.decode( fileName,"UTF-8" );
        System.out.println( "file.getAbsolutePath(); = " + file.getAbsolutePath() );
//        URL url=n
//        URI uri=new URI( );
    }
}
