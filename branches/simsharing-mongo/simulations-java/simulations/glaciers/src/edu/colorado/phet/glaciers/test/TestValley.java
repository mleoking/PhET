// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.glaciers.test;

import edu.colorado.phet.glaciers.model.Valley;


public class TestValley {

    public static void main( String[] args ) {
        Valley valley = new Valley();
        System.out.println( "F(0)=" + valley.getElevation( 0 ) );
        System.out.println( "F(10000)=" + valley.getElevation( 10000 ) );
        System.out.println( "F(70000)=" + valley.getElevation( 70000 ) );
        System.out.println( "F(80000)=" + valley.getElevation( 80000 ) );
    }
}
