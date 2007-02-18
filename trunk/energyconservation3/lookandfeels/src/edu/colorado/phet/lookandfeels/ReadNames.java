package edu.colorado.phet.lookandfeels;

import java.io.File;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 4:15:33 PM
 * Copyright (c) Feb 18, 2007 by Sam Reid
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
