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
class EventLine {
    public final long time;
    public final String object;
    public final String event;
    public final Parameter[] parameters;

    public EventLine( long time, String object, String event, Parameter[] parameters ) {
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

    public String getParameter( String key ) {
        for ( Parameter parameter : parameters ) {
            if ( parameter.name.equals( key ) ) { return parameter.value; }
        }
        throw new RuntimeException( "Parameter not found: " + key );
    }


    //Parse a line that is an event
    public static EventLine parse( String line ) {
        StringTokenizer tokenizer = new StringTokenizer( line, "\t" );
        long time = Long.parseLong( tokenizer.nextToken() );
        String object = tokenizer.nextToken();
        String event = tokenizer.nextToken();
        Parameter[] parameters = tokenizer.hasMoreTokens() ? Parameter.parseParameters( tokenizer.nextToken() ) : new Parameter[0];
        EventLine eventLine = new EventLine( time, object, event, parameters );
        return eventLine;
    }
}