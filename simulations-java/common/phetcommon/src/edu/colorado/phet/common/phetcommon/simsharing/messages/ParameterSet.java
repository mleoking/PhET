// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Set of parameters used in sim sharing.  Key ordering is maintained.  Duplicate keys are not allowed,
 * but setting the same key/value pair is allowed to permit flexibility in tricky sims.
 * <p/>
 * A ParameterSet is immutable, this makes it easier to use a more concise "builder"-like pattern in client code.
 *
 * @author Sam Reid
 */
public class ParameterSet {
    private final ArrayList<Parameter> parameters;


    public ParameterSet() {
        this( new ArrayList<Parameter>() );
    }

    //No defensive copy made here (to go easy on the heap/GC), so clients must take care not to modify these data structures once created.
    public ParameterSet( ArrayList<Parameter> parameters ) {
        this.parameters = parameters;
    }

    public ParameterSet( final Parameter parameter ) {
        this( new ArrayList<Parameter>() {{
            add( parameter );
        }} );
    }

    public ParameterSet( Parameter[] parameters ) {
        this( new ArrayList<Parameter>( Arrays.asList( parameters ) ) );
    }

    public ParameterSet add( final Parameter parameter ) {
        if ( containsKey( parameter.name ) ) {
            if ( !getValue( parameter.name ).equals( parameter.value ) ) {
                throw new RuntimeException( "Parameter name already contained with different value: " + get( parameter.name ) + ", newValue = " + parameter.value );
            }
            else {
                //Nothing to do, key and value already stored.  Do not re-add key to list or it will alter the original ordering.
                return this;
            }
        }
        else {
            return new ParameterSet( new ArrayList<Parameter>( parameters ) {{
                add( parameter );
            }} );
        }
    }

    private String getValue( ParameterKey name ) {
        return get( name ).value;
    }

    public Parameter get( ParameterKey name ) {
        for ( Parameter parameter : parameters ) {
            if ( parameter.name.equals( name ) ) { return parameter; }
        }
        return null;
    }

    private boolean containsKey( ParameterKey name ) {
        return get( name ) != null;
    }

    public String toString( String delimiter ) {
        return new ObservableList<Parameter>( parameters ).mkString( delimiter );
    }

    public ParameterSet param( ParameterKey name, boolean value ) {
        return add( new Parameter( name, value ) );
    }

    public ParameterSet param( ParameterKey name, double value ) {
        return add( new Parameter( name, value ) );
    }

    public ParameterSet param( ParameterKey name, String value ) {
        return add( new Parameter( name, value ) );
    }

    public ParameterSet param( ParameterKey name, int value ) {
        return add( new Parameter( name, value ) );
    }

    public ParameterSet addAll( Parameter[] parameters ) {
        ParameterSet p = this;
        for ( Parameter parameter : parameters ) {
            p = p.add( parameter );
        }
        return p;
    }

    public ParameterSet addAll( ParameterSet param ) {
        return addAll( param.parameters.toArray( new Parameter[param.parameters.size()] ) );
    }
}