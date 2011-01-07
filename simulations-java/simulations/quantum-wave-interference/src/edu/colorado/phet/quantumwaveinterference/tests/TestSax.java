// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.tests;

import javax.xml.parsers.SAXParserFactory;

/**
 * User: Sam Reid
 * Date: Feb 15, 2006
 * Time: 5:51:59 PM
 */

public class TestSax {
    public static void main( String[] args ) {
        String str = "javax.xml.parsers.SAXParserFactory";
        System.out.println( "System.getProperty( str) = " + System.getProperty( str ) );

        SAXParserFactory inst = SAXParserFactory.newInstance();
        System.out.println( "inst = " + inst );
    }
}
