package edu.colorado.phet.common.lookandfeels;

import java.io.File;

/**
 * Utility function for reading theme names for oyoaha (not used during simulation runtime.
 */

public class ReadNames {
    public static void main( String[] args ) {
        File file = new File( "C:\\PhET\\projects\\energyskatepark\\lookandfeels\\data\\Oyoaha.themepack.14.04.05" );
        File[] children = file.listFiles();
        String out = new String();
        for( int i = 0; i < children.length; i++ ) {
            File child = children[i];
            out += "\"" + child.getName() + "\", ";
        }
        System.out.println( out );
    }
}
