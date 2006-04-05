/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2.tests;

import java.io.File;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 3, 2006
 * Time: 12:25:59 AM
 * Copyright (c) Apr 3, 2006 by Sam Reid
 */

public class TestRenameTo {
    public static void main( String[] args ) throws IOException {
        String name = "C:\\Documents and Settings\\Sam Reid\\Application Data\\Sun\\Java\\Deployment\\cache\\javaws\\file\\Dlocalhost\\P-1\\DMC&c\\DMPhET\\DMintellij-projects\\DMPhetLauncher2\\DMphetlauncher-cache\\DMwww.colorado.edu\\DMphysics\\DMphet\\DMsimulations\\DMcck\\AMcck-local.jnlp";
        File file = new File( "C:/test.txt" );
        file.createNewFile();
        boolean ren = file.renameTo( new File( name ) );
        System.out.println( "ren = " + ren );
    }
}
