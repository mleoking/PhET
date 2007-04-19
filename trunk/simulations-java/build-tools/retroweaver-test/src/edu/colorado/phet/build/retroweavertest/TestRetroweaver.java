package edu.colorado.phet.build.retroweavertest;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Apr 18, 2007, 10:25:25 PM
 */
public class TestRetroweaver {
    public static void main( String[] args ) {
        ArrayList<String> myList = new ArrayList<String>();
        myList.add( "hello" );
        String element = myList.get( 0 );
        System.out.println( "element 0 =" + element );

        //uncomment the following line to test API dependency check in retroweaver
//        ProcessBuilder processBuilder = new ProcessBuilder( "a", "b", "c" );
    }
}
