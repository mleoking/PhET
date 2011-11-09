// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.Arrays;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * A single line that represents an event.
 *
 * @author Sam Reid
 */
public class JavaEntry {
    public final long timeMilliSec;
    public final String actor;
    public final String event;
    public final Parameter[] parameters;
    public final double time;

    //Use this constructor for matching only
    public JavaEntry( String actor, String event, Parameter... parameters ) {
        this( -1, actor, event, parameters );
    }

    public JavaEntry( long timeMilliSec, String actor, String event, Parameter[] parameters ) {
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

    public boolean matches( String obj, String act ) {
        return matches( obj, act, new Parameter[0] );
    }

    public boolean matches( String obj, Parameter[] params ) {
        for ( Parameter param : params ) {
            if ( !hasParameter( param ) ) {
                return false;
            }
        }
        return actor.equals( obj );
    }

    public boolean matches( String obj, String act, Parameter[] params ) {
        for ( Parameter param : params ) {
            if ( !hasParameter( param ) ) {
                return false;
            }
        }
        return actor.equals( obj ) && event.equals( act );
    }

    private boolean hasParameter( Parameter param ) {
        return hasParameterKey( param.name ) && get( param.name ).get().equals( param.value );
    }

    private boolean hasParameterKey( String name ) {

        for ( Parameter parameter : parameters ) {
            if ( parameter.name.equals( name ) ) { return true; }
        }
        return false;
    }

    public Option<String> apply( String key ) {
        return get( key );
    }

    public Option<String> get( String key ) {
        for ( Parameter parameter : parameters ) {
            if ( parameter.name.equals( key ) ) { return new Option.Some<String>( parameter.value ); }
        }
//        new RuntimeException( "Parameter not found: " + key + " in parameters: " + Arrays.asList( parameters ) ).printStackTrace();
        return new Option.None<String>();
    }

    //Parse a line that is an event
    public static JavaEntry parse( String line ) {
        StringTokenizer tokenizer = new StringTokenizer( line, "\t" );
        long time = Long.parseLong( tokenizer.nextToken() );
        String object = tokenizer.nextToken();
        String event = tokenizer.nextToken();

        //Transform the end of the line back into a parseable version
        StringBuffer remainderOfLineBuf = new StringBuffer();
        while ( tokenizer.hasMoreTokens() ) {
            remainderOfLineBuf.append( tokenizer.nextToken() );
            remainderOfLineBuf.append( Parameter.DELIMITER );
        }
        String remainderOfLine = remainderOfLineBuf.toString().trim();
        Parameter[] parameters = Parameter.parseParameters( remainderOfLine );

        //Return the new entry
        return new JavaEntry( time, object, event, parameters );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        JavaEntry entry = (JavaEntry) o;

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

    public boolean hasParameter(String key,String value){
        for ( Parameter parameter : parameters ) {
            if (parameter.name.equals( key )&& parameter.value.equals( value )) return true;
        }
        return false;
    }
}