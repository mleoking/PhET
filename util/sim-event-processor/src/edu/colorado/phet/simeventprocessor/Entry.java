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
class Entry {
    public final long time;
    public final String object;
    public final String event;
    public final Parameter[] parameters;

    public Entry( long time, String object, String event, Parameter[] parameters ) {
        this.time = time;
        this.object = object;
        this.event = event;
        this.parameters = parameters;
    }

    @Override public String toString() {
        return "EventLine{" +
               "time=" + time +
               ", object='" + object + '\'' +
               ", event='" + event + '\'' +
               ", parameters=" + ( parameters == null ? null : Arrays.asList( parameters ) ) +
               '}';
    }

    public boolean matches( String obj, String act ) {
        return object.equals( obj ) && event.equals( act );
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

        if ( time != entry.time ) { return false; }
        if ( event != null ? !event.equals( entry.event ) : entry.event != null ) { return false; }
        if ( object != null ? !object.equals( entry.object ) : entry.object != null ) { return false; }
        if ( !Arrays.equals( parameters, entry.parameters ) ) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) ( time ^ ( time >>> 32 ) );
        result = 31 * result + ( object != null ? object.hashCode() : 0 );
        result = 31 * result + ( event != null ? event.hashCode() : 0 );
        result = 31 * result + ( parameters != null ? Arrays.hashCode( parameters ) : 0 );
        return result;
    }

    public String brief() {
        return object + " " + event + ( object.equals( "button node" ) ? ": " + get( "actionCommand" ) : "" );
    }
}