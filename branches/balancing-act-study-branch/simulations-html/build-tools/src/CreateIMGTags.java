package edu.colorado.phet.buildtools.html5;

import java.io.File;

public class CreateIMGTags {
    public static void main( String[] args ) {
        File dir = new File( args[0] );
        for ( int i = 0; i < dir.listFiles().length; i++ ) {
            File file = dir.listFiles()[i];
            String lowername = file.getName().toLowerCase();
            if ( lowername.endsWith( "png" ) ||
                 lowername.endsWith( "jpg" ) ) {
                System.out.println( "<img src=\"images/" + file.getName() + "\"/>" );
            }
        }
    }
}
