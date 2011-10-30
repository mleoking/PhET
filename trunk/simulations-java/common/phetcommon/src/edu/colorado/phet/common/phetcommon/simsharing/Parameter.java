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

    public Parameter( String name, boolean value ) {
        this( name, value + "" );
    }

    public Parameter( String name, double value ) {
        this( name, value + "" );
    }

    public Parameter( String name, long value ) {
        this( name, value + "" );
    }

    public Parameter( String name, String value ) {
        this.name = name;
        this.value = value;
    }

    @Override public String toString() {
        return name + " = " + value;
    }

    public static Parameter param( String name, double value ) {
        return new Parameter( name, value );
    }

    public static Parameter param( String name, boolean value ) {
        return new Parameter( name, value );
    }

    public static Parameter param( String name, long value ) {
        return new Parameter( name, value );
    }

    public static Parameter param( String name, String value ) {
        return new Parameter( name, value );
    }
}