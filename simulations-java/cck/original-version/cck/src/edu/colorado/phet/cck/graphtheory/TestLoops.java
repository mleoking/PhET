/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.graphtheory;

import java.util.Arrays;
import java.util.List;

/**
 * User: Sam Reid
 * Date: Aug 31, 2003
 * Time: 11:46:31 AM
 * Copyright (c) Aug 31, 2003 by Sam Reid
 */
public class TestLoops {
    public static void main( String[] args ) {
        Graph g = new Graph();
        g.addVertex( "0" );
        g.addVertex( "1" );
        g.addVertex( "2" );
        g.addVertex( "3" );
        g.addConnection( 0, 1 );
        g.addConnection( 1, 2 );
        g.addConnection( 2, 0 );
        g.addConnection( 3, 0 );
        g.addConnection( 1, 3 );
        g.addConnection( 3, 2 );

        System.out.println( "g = " + g );
        DirectedEdge[] edges = g.getEdges( 0 );
        List list = Arrays.asList( edges );
        System.out.println( "list = " + list );

        Loop[] paths = g.getLoops();
        List pathList = Arrays.asList( paths );
        System.out.println( "pathList = " + pathList );
        Loop zero = paths[0];
        for( int i = 0; i < paths.length; i++ ) {
            boolean equal = zero.equals( paths[i] );
            System.out.println( "equal[" + i + "] = " + equal );
        }
        Loop other = new Loop( new DirectedPath( new DirectedPathElement( g.edgeAt( 0 ), true ) ) );
        boolean equal = zero.equals( other );
        System.out.println( "equal[" + other + "] = " + equal );
    }

    public static void test1( String[] args ) {
        Graph g = new Graph();
        g.addVertex( "0" );
        g.addVertex( "1" );
        g.addVertex( "2" );
        g.addConnection( 0, 1 );
        g.addConnection( 1, 2 );
        g.addConnection( 2, 0 );

        System.out.println( "g = " + g );
        DirectedEdge[] edges = g.getEdges( 0 );
        List list = Arrays.asList( edges );
        System.out.println( "list = " + list );

        Loop[] paths = g.getLoops();
        List pathList = Arrays.asList( paths );
        System.out.println( "pathList = " + pathList );
        Loop zero = paths[0];
        for( int i = 0; i < paths.length; i++ ) {
            boolean equal = zero.equals( paths[i] );
            System.out.println( "equal[" + i + "] = " + equal );
        }
        Loop other = new Loop( new DirectedPath( new DirectedPathElement( g.edgeAt( 0 ), true ) ) );
        boolean equal = zero.equals( other );
        System.out.println( "equal[" + other + "] = " + equal );
    }
}
