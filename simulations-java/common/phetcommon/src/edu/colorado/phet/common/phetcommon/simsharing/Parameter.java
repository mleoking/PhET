// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterValue;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.componentType;

/**
 * Parameter portion of a sim-sharing event.
 * Standardizes the rules for formatting parameters, so that we can also standardize on a parser.
 * Parameters cannot contain the delimiter character that is used in their conversion to string.
 *
 * @author Sam Reid
 */
public class Parameter {

    public final ParameterKey name;

    //Values can be anything, so just use plain string class and do not try to constrain or make it easy to identify
    public final String value;

    public Parameter( ParameterKey name, boolean value ) {
        this( name, value + "" );
    }

    public Parameter( ParameterKey name, double value ) {
        this( name, value + "" );
    }

    public Parameter( ParameterKey name, long value ) {
        this( name, value + "" );
    }

    public Parameter( ParameterKey name, String value ) {
        this.name = name;
        this.value = value;
    }

    @Override public String toString() {
        return name + " = " + value;
    }

    public static ParameterSet param( ParameterKey name, double value ) {
        return new ParameterSet( new Parameter( name, value ) );
    }

    public static ParameterSet param( ParameterKey name, ParameterValue value ) {
        return new ParameterSet( new Parameter( name, value.toString() ) );
    }

    public static ParameterSet param( ParameterKey name, boolean value ) {
        return new ParameterSet( new Parameter( name, value ) );
    }

    public static ParameterSet componentType( ComponentType component ) {
        return new ParameterSet( new Parameter( componentType, component + "" ) );
    }

    public static ParameterSet param( ParameterKey name, long value ) {
        return new ParameterSet( new Parameter( name, value ) );
    }

    public static ParameterSet param( ParameterKey name, String value ) {
        return new ParameterSet( new Parameter( name, value ) );
    }

    // Appends additional parameters to an array of parameters.
    public static Parameter[] appendParameters( Parameter[] parameters, Parameter... additionalParameters ) {
        ArrayList<Parameter> parameterList = new ArrayList<Parameter>( Arrays.asList( parameters ) );
        for ( Parameter parameter : additionalParameters ) {
            parameterList.add( parameter );
        }
        return parameterList.toArray( new Parameter[parameterList.size()] );
    }

    //Parses a String into a list of parameters, used by the post-processor
    public static Parameter[] parseParameters( String line, String delimiter ) {
        StringTokenizer st = new StringTokenizer( line, delimiter );
        ArrayList<Parameter> parameters = new ArrayList<Parameter>();
        while ( st.hasMoreTokens() ) {
            parameters.add( parseParameter( st.nextToken() ) );
        }
        return parameters.toArray( new Parameter[parameters.size()] );
    }

    private static Parameter parseParameter( String s ) {
        int index = s.indexOf( '=' );
        final String parsed = s.substring( 0, index ).trim();
        return new Parameter( new ParsedParameter( parsed ), s.substring( index + 1 ).trim() );
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

    /**
     * This class allows us to represent a parsed parameter key.  This is required since IParameterKey is a marker interface
     * that makes it easy to identify the source of different parameters.  This parameter is one that was parsed during postprocessing/analysis.
     */
    private static class ParsedParameter implements ParameterKey {
        private final String parsed;

        public ParsedParameter( String parsed ) {
            this.parsed = parsed;
        }

        @Override public String toString() {
            return parsed;
        }
    }
}