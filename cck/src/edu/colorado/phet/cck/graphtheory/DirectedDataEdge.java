/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.graphtheory;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 1:19:02 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class DirectedDataEdge extends DirectedEdge {
    private Object data;

    public DirectedDataEdge( Object source, Object destination, Object data ) {
        super( source, destination );
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
