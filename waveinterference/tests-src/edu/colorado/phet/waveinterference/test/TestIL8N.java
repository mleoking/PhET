/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.test;

import edu.colorado.phet.common.view.util.SimStrings;

import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: May 22, 2006
 * Time: 10:51:19 PM
 * Copyright (c) May 22, 2006 by Sam Reid
 */

public class TestIL8N {
    public static void main( String[] args ) {
        SimStrings.init( args, "LabelsBundle" );
        String units = "cm";
        String hello = MessageFormat.format( SimStrings.get( "hello.0" ), new Object[]{units} );
        System.out.println( "hello = " + hello );
    }
}
