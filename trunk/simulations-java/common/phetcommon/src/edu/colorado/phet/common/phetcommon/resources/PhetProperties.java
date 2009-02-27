/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.resources;

import java.util.Properties;

import edu.colorado.phet.common.phetcommon.view.util.StringUtil;

/**
 * An extension of Properties that provides some convenience methods.
 */
public class PhetProperties extends Properties {

    public PhetProperties() {
        super();
    }

    public PhetProperties( Properties properties ) {
        super( properties );
    }

    public String getString( String propertyName ) {
        String s = super.getProperty( propertyName );
        if ( s == null ) {
            System.err.println( "WARNING: requested property not found: " + propertyName );
            s = propertyName;
        }
        return s;
    }

    public int getInt( String key, int defaultValue ) {
        return StringUtil.asInt( getString( key ), defaultValue );
    }

    public double getDouble( String key, double defaultValue ) {
        return StringUtil.asDouble( getString( key ), defaultValue );
    }
    
    public long getLong( String key, long defaultValue ) {
        return StringUtil.asLong( getString( key ), defaultValue );
    }

    public char getChar( String key, char defaultValue ) {
        return StringUtil.asChar( getString( key ), defaultValue );
    }
}
