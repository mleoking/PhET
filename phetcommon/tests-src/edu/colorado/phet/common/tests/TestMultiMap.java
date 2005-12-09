/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.util.MultiMap;

import java.util.Iterator;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 9:17:45 PM
 * Copyright (c) Dec 12, 2004 by Sam Reid
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
        while( i.hasNext() ) {
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
        while( i.hasNext() ) {
            System.out.println( i.next() );
        }

        Iterator ri = mm.reverseIterator();

        ri = mm.reverseIterator();
        System.out.println( "" );
        while( ri.hasNext() ) {
            System.out.println( ri.next() );
        }

        mm.remove( "c" );
        System.out.println( "" );
        i = mm.iterator();
        while( i.hasNext() ) {
            System.out.println( i.next() );
        }


    }
}