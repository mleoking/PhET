// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

/**
 * Parameter portion of a sim-sharing event.
 * Standardizes the rules for formatting parameters, so that we can also standardize on a parser.
 * Parameters cannot contain the delimiter character that is used in their conversion to string.
 *
 * @author Sam Reid
 */
public class Parameter {

    public final IParameterKey name;

    //Values can be anything, so just use plain string class and do not try to constrain or make it easy to identify
    public final String value;

    public Parameter( IParameterKey name, IParameterValue value ) {
        this( name, value.toString() );
    }

    public Parameter( IParameterKey name, boolean value ) {
        this( name, value + "" );
    }

    public Parameter( IParameterKey name, double value ) {
        this( name, value + "" );
    }

    public Parameter( IParameterKey name, long value ) {
        this( name, value + "" );
    }

    public Parameter( IParameterKey name, String value ) {
        this.name = name;
        this.value = value;
    }

    @Override public String toString() {
        return name + " = " + value;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        Parameter parameter = (Parameter) o;

        if ( name != null ? !name.equals( parameter.name ) : parameter.name != null ) { return false; }
        if ( value != null ? !value.equals( parameter.value ) : parameter.value != null ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( value != null ? value.hashCode() : 0 );
        return result;
    }
}