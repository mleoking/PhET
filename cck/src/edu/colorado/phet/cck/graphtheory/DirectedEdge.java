/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.graphtheory;


/**
 * User: Sam Reid
 * Date: Aug 31, 2003
 * Time: 4:40:00 AM
 * Copyright (c) Aug 31, 2003 by Sam Reid
 */
public class DirectedEdge {
    Object source;
    Object destination;

    public DirectedEdge( Object source, Object destination ) {
        this.source = source;
        this.destination = destination;
    }

    public Object getSource() {
        return source;
    }

    public Object getDestination() {
        return destination;
    }

    public boolean containsVertex( Object vertex ) {
        return source == vertex || destination == vertex;
    }

    public String toString() {
        return "[" + source + ", " + destination + "]";
    }

    public Object getOppositeVertex( Object a ) {
        if( source == a ) {
            return destination;
        }
        else if( destination == a ) {
            return source;
        }
        throw new RuntimeException( "No such vertex." );
    }

}
