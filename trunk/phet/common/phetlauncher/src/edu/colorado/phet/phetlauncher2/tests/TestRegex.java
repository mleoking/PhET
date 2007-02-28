/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2.tests;

import java.io.File;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 11:46:53 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class TestRegex {
    public static void main( String[] args ) {
        String str = "codebase=\"http://www.colorado.edu/physics/phet/simulations\"";
        File file = new File( "C:\\PhET\\intellij-projects\\PhetLauncher2\\phetlauncher-cache" );
        String rep = file.getAbsolutePath();
        rep = rep.replace( '\\', '/' );
        str = str.replaceAll( "codebase=\"http://", rep );
        System.out.println( "str = " + str );
    }
}
