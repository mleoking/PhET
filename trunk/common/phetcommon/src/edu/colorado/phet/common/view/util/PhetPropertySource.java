/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

/**
 * Represents a source for property information.
 */
public interface PhetPropertySource {
    String getString( String propertyName );

    int getInt( String key, int defaultValue );

    double getDouble( String key, double defaultValue );

    char getChar( String key, char defaultValue );
}
