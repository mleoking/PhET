// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.io.IOException;

/**
 * @author Sam Reid
 */
public class MultiLoadTester {
    public static void main( String[] args ) throws IOException, InterruptedException {
        String command = "java -classpath C:\\workingcopy\\phet\\svn\\trunk\\util\\simsharing\\deploy\\simsharing_all.jar edu.colorado.phet.common.phetcommon.simsharing.tests.LoadTester 60 -study phetdev";
        for ( int i = 0; i < 50; i++ ) {
            Process p = Runtime.getRuntime().exec( command );
            Thread.sleep( 500 );

            System.out.println( "Launched i=" + i );
        }
    }
}
