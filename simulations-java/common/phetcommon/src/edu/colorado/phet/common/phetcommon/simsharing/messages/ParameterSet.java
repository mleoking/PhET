// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;

/**
 * Set of parameters used in sim sharing.  Key ordering is maintained.  Duplicate keys are not allowed,
 * but setting the same key/value pair is allowed to permit flexibility in tricky sims.
 * <p/>
 * A ParameterSet is immutable, this makes it easier to use a more concise "builder"-like pattern in client code.
 *
 * @author Sam Reid
 */
public class ParameterSet {
    private final ArrayList<ParameterKey> keys;
    private final HashMap<ParameterKey, String> map;

    public ParameterSet() {
        this( new ArrayList<ParameterKey>(), new HashMap<ParameterKey, String>() );
    }

    //No defensive copy made here (to go easy on the heap/GC), so clients must take care not to modify these data structures once created.
    public ParameterSet( ArrayList<ParameterKey> keys, HashMap<ParameterKey, String> map ) {
        this.keys = keys;
        this.map = map;
    }

    public ParameterSet add( final Parameter parameter ) {
        if ( map.containsKey( parameter.name ) ) {
            if ( !map.get( parameter.name ).equals( parameter.value ) ) {
                throw new RuntimeException( "Parameter name already contained with different value: " + map.get( parameter.name ) + ", newValue = " + parameter.value );
            }
            else {
                //Nothing to do, key and value already stored.  Do not re-add key to list or it will alter the original ordering.
                return this;
            }
        }
        else {
            ArrayList<ParameterKey> newKeys = new ArrayList<ParameterKey>( keys ) {{
                add( parameter.name );
            }};
            HashMap<ParameterKey, String> newMap = new HashMap<ParameterKey, String>( map ) {{
                put( parameter.name, parameter.value );
            }};
            return new ParameterSet( newKeys, newMap );
        }
    }
}