// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.resources;

import junit.framework.TestCase;

/**
 * The test suite for PhetProperties.
 */
public class ZPhetPropertiesTester extends TestCase {
    PhetProperties properties;

    public void setUp() {
        properties = new PhetProperties();

        properties.put( "string", "yoda" );
        properties.put( "double", "3.1415" );
        properties.put( "int", "1" );
        properties.put( "char", "a" );
    }

    public void testGetStringThatExists() {
        assertEquals( "yoda", properties.getString( "string" ) );
    }

    public void testGetStringThatDoesNotExistEqualsPropertyName() {
        assertEquals( "yoda", properties.getString( "yoda" ) );
    }

    public void testGetInt() {
        assertEquals( 1, properties.getInt( "int", -1 ) );
    }

    public void testGetDouble() {
        assertEquals( 3.1415, properties.getDouble( "double", 0.0 ), 0.0 );
    }

    public void testGetChar() {
        assertEquals( 'a', properties.getChar( "char", 'b' ) );
    }
}