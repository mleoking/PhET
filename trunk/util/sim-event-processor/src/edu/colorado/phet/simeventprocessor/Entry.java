// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.Arrays;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;

/**
 * A single line that represents an event.
 *
 * @author Sam Reid
 */
public class Entry {
    public final long timeMilliSec;
    public final String actor;
    public final String event;
    public final Parameter[] parameters;
    public final double time;

    //Use this constructor for matching only
    public Entry( String actor, String event, Parameter... parameters ) {
        this( -1, actor, event, parameters );
    }

    public Entry( long timeMilliSec, String actor, String event, Parameter[] parameters ) {
        this.actor = actor;
        this.event = event;
        this.parameters = parameters;
        this.timeMilliSec = timeMilliSec;
        this.time = timeMilliSec / 1000.0;
    }

    @Override public String toString() {
        return "EventLine{" +
               "time=" + timeMilliSec +
               ", object='" + actor + '\'' +
               ", event='" + event + '\'' +
               ", parameters=" + ( parameters == null ? null : Arrays.asList( parameters ) ) +
               '}';
    }

    public boolean matches( String obj, String act, Parameter... params ) {
        for ( Parameter param : params ) {
            if ( !hasParameter( param ) ) {
                return false;
            }
        }
        return actor.equals( obj ) && event.equals( act );
    }

    private boolean hasParameter( Parameter param ) {
        return hasParameterKey( param.name ) && get( param.name ).equals( param.value );
    }

    private boolean hasParameterKey( String name ) {

        for ( Parameter parameter : parameters ) {
            if ( parameter.name.equals( name ) ) { return true; }
        }
        return false;
    }

    public String apply( String key ) {
        return get( key );
    }

    public String get( String key ) {
        for ( Parameter parameter : parameters ) {
            if ( parameter.name.equals( key ) ) { return parameter.value; }
        }
        throw new RuntimeException( "Parameter not found: " + key );
    }


    //Parse a line that is an event
    public static Entry parse( String line ) {
        StringTokenizer tokenizer = new StringTokenizer( line, "\t" );
        long time = Long.parseLong( tokenizer.nextToken() );
        String object = tokenizer.nextToken();
        String event = tokenizer.nextToken();
        Parameter[] parameters = tokenizer.hasMoreTokens() ? Parameter.parseParameters( tokenizer.nextToken() ) : new Parameter[0];
        Entry entry = new Entry( time, object, event, parameters );
        return entry;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        Entry entry = (Entry) o;

        if ( timeMilliSec != entry.timeMilliSec ) { return false; }
        if ( event != null ? !event.equals( entry.event ) : entry.event != null ) { return false; }
        if ( actor != null ? !actor.equals( entry.actor ) : entry.actor != null ) { return false; }
        if ( !Arrays.equals( parameters, entry.parameters ) ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) ( timeMilliSec ^ ( timeMilliSec >>> 32 ) );
        result = 31 * result + ( actor != null ? actor.hashCode() : 0 );
        result = 31 * result + ( event != null ? event.hashCode() : 0 );
        result = 31 * result + ( parameters != null ? Arrays.hashCode( parameters ) : 0 );
        return result;
    }

    public String brief() {
        return actor + " " + event + ( actor.equals( "button node" ) ? ": " + get( "actionCommand" ) : "" );
    }
}