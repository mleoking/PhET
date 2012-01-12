// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.common.phetcommon.tests.utils;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 8, 2006
 * Time: 1:48:42 PM
 */

public class TestDefaultDecimalFormat {
    public static void main( String[] args ) {
        System.out.println( new DefaultDecimalFormat( "0.00" ).format( -0.00 ) );
    }
}
