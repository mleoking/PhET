/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

import java.util.Properties;

/**
 * An extension of Properties to provide support for the PhetPropertySource
 * interface.
 */
public class PropertiesEx extends Properties implements PhetPropertySource {
    public PropertiesEx() {
        super();
    }

    public PropertiesEx( Properties properties ) {
        super( properties );
    }

    public String getString( String propertyName ) {
        String s = super.getProperty( propertyName );

        if (s == null) return propertyName;

        return s;
    }

    public int getInt( String key, int defaultValue ) {
        return StringUtil.asInt( getString( key ), defaultValue );
    }

    public double getDouble( String key, double defaultValue ) {
        return StringUtil.asDouble( getString( key ), defaultValue );
    }

    public char getChar( String key, char defaultValue ) {
        return StringUtil.asChar( getString( key ), defaultValue );
    }
}
