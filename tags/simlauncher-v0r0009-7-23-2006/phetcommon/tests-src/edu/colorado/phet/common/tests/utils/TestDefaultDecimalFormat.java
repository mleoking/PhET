/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.utils;

import edu.colorado.phet.common.util.DefaultDecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 8, 2006
 * Time: 1:48:42 PM
 * Copyright (c) Jun 8, 2006 by Sam Reid
 */

public class TestDefaultDecimalFormat {
    public static void main( String[] args ) {
        System.out.println( new DefaultDecimalFormat( "0.00" ).format( -0.00 ) );
    }
}
