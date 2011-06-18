// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14677 $
 * Date modified : $Date:2007-04-17 03:40:29 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.phetcommon.tests;

import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.util.MultiMap;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 9:17:45 PM
 */

public class TestMultiMap {
    public static void main( String[] args ) {
        MultiMap mm = new MultiMap();
        mm.put( "a", "1" );
        mm.put( "c", "4" );
        mm.put( "c", "5" );
        mm.put( "a", "2" );
        mm.put( "a", "3" );
        mm.put( "c", "6" );

        Iterator i = mm.iterator();
        while ( i.hasNext() ) {
            System.out.println( i.next() );
        }
        System.out.println( "contains(4): " + mm.containsValue( "4" ) );

        i = mm.iterator();
//        while( i.hasNext() ) {
////            if( i.next().equals( "6" ) ) {
////                i.remove();
////                break;
////            }
//        }

        System.out.println( "" );
        i = mm.iterator();
        while ( i.hasNext() ) {
            System.out.println( i.next() );
        }

        Iterator ri = mm.reverseIterator();

        ri = mm.reverseIterator();
        System.out.println( "" );
        while ( ri.hasNext() ) {
            System.out.println( ri.next() );
        }

        mm.remove( "c" );
        System.out.println( "" );
        i = mm.iterator();
        while ( i.hasNext() ) {
            System.out.println( i.next() );
        }


    }
}