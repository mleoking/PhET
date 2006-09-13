package edu.colorado.phet.cck3.tests;

import java.io.File;

/**
 * User: Sam Reid
 * Date: Aug 11, 2006
 * Time: 8:57:21 AM
 * Copyright (c) Aug 11, 2006 by Sam Reid
 */

public class MultimediaList {
    public static void main( String[] args ) {
        File dir = new File( "C:\\PhET\\projects\\cck\\data\\images" );
        File[]children = dir.listFiles();
        for( int i = 0; i < children.length; i++ ) {
            File child = children[i];
            System.out.println( child.getName() );
        }
    }
}
