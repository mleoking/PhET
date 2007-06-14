package edu.colorado.phet.mm;

import java.io.File;

/**
 * User: Sam Reid
 * Date: Aug 17, 2006
 * Time: 10:13:58 AM
 * Copyright (c) Aug 17, 2006 by Sam Reid
 */

public class TestPath {
    public static void main( String[] args ) {
        File parent = new File( "C:\\PhET\\projects\\phet-mm" );
        File child = new File( "phet-mm-temp\\Band Structure\\images\\clock.png" );
        String path = MultimediaApplication.getPath( parent, child );
        System.out.println( "path = " + path );
    }
}
