// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

/**
 * ID for named user interface components to be chained with ComponentChain
 *
 * @author Sam Reid
 */
public class StringUserComponent implements UserComponent {
    public final String name;

    public StringUserComponent( String name ) {
        this.name = name;
    }

    @Override public String toString() {
        return name;
    }
}