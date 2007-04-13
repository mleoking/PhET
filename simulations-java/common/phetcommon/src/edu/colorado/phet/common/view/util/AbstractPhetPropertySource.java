/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

public abstract class AbstractPhetPropertySource implements PhetPropertySource {
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
