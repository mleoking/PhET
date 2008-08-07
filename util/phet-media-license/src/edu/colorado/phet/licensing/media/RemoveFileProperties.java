package edu.colorado.phet.licensing.media;

import java.io.*;
import java.util.Properties;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 2:25:27 AM
 */
public class RemoveFileProperties {
    public static void main( String[] args ) throws IOException {
        File[] file = new File( "C:\\phet\\subversion\\trunk\\simulations-java\\misc\\phet-media\\annotated-data" ).listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.toLowerCase().endsWith( "properties" );
            }
        } );
        for ( int i = 0; i < file.length; i++ ) {
            File file1 = file[i];
            Properties properties = new Properties();
            properties.load( new FileInputStream( file1 ) );
            properties.remove( "filename" );
            properties.store( new FileOutputStream( file1 ), null );
        }
    }
}
