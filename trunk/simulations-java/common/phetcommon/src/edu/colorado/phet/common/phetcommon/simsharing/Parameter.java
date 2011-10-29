// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

/**
 * Standardizes the rules for formatting action parameters, so that we can also standardize on a parser.
 *
 * @author Sam Reid
 */
public class Parameter {
    public final String name;
    public final String value;

    public Parameter( String name, String value ) {
        this.name = name;
        this.value = value;
    }

    @Override public String toString() {
        return name + " = " + value;
    }
}