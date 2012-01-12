// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.unfuddle;

import org.w3c.dom.Node;

/**
 * @author Sam Reid
 */
class UnfuddleComponent extends XMLObject {

    public UnfuddleComponent( Node node ) {
        super( node );
    }

    public int getID() {
        return Integer.parseInt( getTextContent( "id" ) );
    }

    public String getName() {
        return getTextContent( "name" );
    }

    public String toString() {
        return getName();
    }
}
