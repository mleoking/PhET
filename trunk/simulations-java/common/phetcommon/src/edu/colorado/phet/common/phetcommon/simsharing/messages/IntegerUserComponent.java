// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

/**
 * ID for number user interface components to be chained with ComponentChain
 *
 * @author Sam Reid
 */
public class IntegerUserComponent implements UserComponent {
    public final int index;

    public IntegerUserComponent( int index ) {
        this.index = index;
    }

    @Override public String toString() {
        return index + "";
    }
}
