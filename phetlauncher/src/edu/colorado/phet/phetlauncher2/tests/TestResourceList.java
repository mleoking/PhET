/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2.tests;

import netx.jnlp.JNLPFile;
import netx.jnlp.ParseException;
import netx.jnlp.ResourcesDesc;

import java.io.File;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 10:00:50 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class TestResourceList {
    public static void main( String[] args ) throws IOException, ParseException {
        String str = "C:\\PhET\\intellij-projects\\PhetLauncher2\\phetlauncher-cache\\www.colorado.edu\\physics\\phet\\simulations\\cck\\cck.jnlp";
        JNLPFile jnlpFile = new JNLPFile( new File( str ).toURL() );
        ResourcesDesc res = jnlpFile.getResources();
        for( int i = 0; i < res.getJARs().length; i++ ) {
            System.out.println( "res.getJARs()[i].getLocation() = " + res.getJARs()[i].getLocation() );
        }
    }
}
