/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

/**
 * User: Sam Reid
 * Date: Apr 3, 2006
 * Time: 6:46:59 PM
 * Copyright (c) Apr 3, 2006 by Sam Reid
 */

public class PhetLauncherUtil {
    public static boolean isMacOSX() {
//        "os.arch"	Operating system architecture
//"os.name"	Operating system name
//"os.version"	Operating system version
//        String osname = System.getProperty( "os.name" );
//        String osVersion = System.getProperty( "os.version" );

        String lcOSName = System.getProperty( "os.name" ).toLowerCase();
        return lcOSName.startsWith( "mac os x" );
    }
}
