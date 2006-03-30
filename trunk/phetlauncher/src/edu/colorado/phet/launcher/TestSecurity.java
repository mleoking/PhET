/* Copyright 2004, Sam Reid */
package edu.colorado.phet.launcher;

/**
 * User: Sam Reid
 * Date: Mar 30, 2006
 * Time: 12:18:38 AM
 * Copyright (c) Mar 30, 2006 by Sam Reid
 */

public class TestSecurity {
    public static void main( String[] args ) {
        System.out.println( "System.getSecurityManager() = " + System.getSecurityManager() );
        System.getProperty( "hello" );
    }
}
