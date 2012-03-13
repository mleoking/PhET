// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * An immutable of parameters used in sim sharing.  Key ordering is maintained.  Duplicate keys are not allowed,
 * but setting the same key/value pair is allowed to permit flexibility in tricky sims.
 *
 * @author Sam Reid
 */
public class ParameterSet implements Iterable<Parameter> {
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

    // Factory methods for ParameterSet
    public static ParameterSet parameterSet( IParameterKey name, double value ) {
        return new ParameterSet( new Parameter( name, value ) );
    }

    public static ParameterSet parameterSet( IParameterKey name, IParameterValue value ) {
        return new ParameterSet( new Parameter( name, value ) );
    }

    public static ParameterSet parameterSet( IParameterKey name, boolean value ) {
        return new ParameterSet( new Parameter( name, value ) );
    }

    public static ParameterSet parameterSet( IParameterKey name, long value ) {
        return new ParameterSet( new Parameter( name, value ) );
    }

    public static ParameterSet parameterSet( IParameterKey name, String value ) {
        return new ParameterSet( new Parameter( name, value ) );
    }

    private String getValue( IParameterKey name ) {
        return get( name ).value;
    }

    public Parameter get( IParameterKey name ) {
        for ( Parameter parameter : parameters ) {
            if ( parameter.name.equals( name ) ) { return parameter; }
        }
        return null;
    }

    private boolean containsKey( IParameterKey name ) {
        return get( name ) != null;
    }

    public String toString( String delimiter ) {
        return new ObservableList<Parameter>( parameters ).mkString( delimiter );
    }

    // Various methods for adding parameters to a parameter set

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

    public ParameterSet add( IParameterKey name, IParameterValue value ) {
        return add( new Parameter( name, value ) );
    }

    public ParameterSet add( IParameterKey name, boolean value ) {
        return add( new Parameter( name, value ) );
    }

    public ParameterSet add( IParameterKey name, double value ) {
        return add( new Parameter( name, value ) );
    }

    public ParameterSet add( IParameterKey name, String value ) {
        return add( new Parameter( name, value ) );
    }

    public ParameterSet add( IParameterKey name, int value ) {
        return add( new Parameter( name, value ) );
    }

    public ParameterSet add( Parameter[] parameters ) {
        ParameterSet p = this;
        for ( Parameter parameter : parameters ) {
            p = p.add( parameter );
        }
        return p;
    }

    public ParameterSet add( ParameterSet param ) {
        return add( param.parameters.toArray( new Parameter[param.parameters.size()] ) );
    }

    public Iterator<Parameter> iterator() {
        return parameters.iterator();
    }

    public static void main( String[] args ) {
        ParameterSet set1 = ParameterSet.parameterSet( ParameterKeys.value, 1 ).add( ParameterKeys.x, 2 ).add( ParameterKeys.y, 2 );
        ParameterSet set2 = new ParameterSet() {{
            add( ParameterKeys.value, 1 );
            add( ParameterKeys.x, 2 );
            add( ParameterKeys.y, 2 );
        }};
        final String delimiter = " ";
        assert ( set1.toString( delimiter ).equals( set2.toString( delimiter ) ) );
        System.out.print( "test passed, happy days!" );
    }
}