/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.graphtheory;

/**
 * User: Sam Reid
 * Date: Sep 2, 2003
 * Time: 1:32:35 AM
 * Copyright (c) Sep 2, 2003 by Sam Reid
 */
public class DirectedPathElement {
    DirectedDataEdge edge;
    boolean isForward;

    public DirectedPathElement( DirectedDataEdge edge, boolean forward ) {
        this.edge = edge;
        isForward = forward;
    }

    public String toString() {
        return "<" + getStartVertex() + " -> " + getEndVertex() + ">";
    }

    public Object getEndVertex() {
        return edge.getOppositeVertex( getStartVertex() );
    }

    public DirectedDataEdge getEdge() {
        return edge;
    }

    public boolean isForward() {
        return isForward;
    }

    public Object getStartVertex() {
        if( isForward ) {
            return edge.getSource();
        }
        else {
            return edge.getDestination();
        }
    }
}
