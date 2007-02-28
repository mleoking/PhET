/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2.tests;

import netx.jnlp.JNLPFile;
import netx.jnlp.ParseException;

import java.io.File;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 5:31:58 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class TestJNLP {
    public static void main( String[] args ) throws IOException, ParseException {
        File local = new File( "C:\\PhET\\intellij-projects\\PhetLauncher2\\phetlauncher-cache\\www.colorado.edu\\physics\\phet\\simulations\\cck\\cck.jnlp" );
        JNLPFile jnlpFile = new JNLPFile( local.toURL() );
        System.out.println( "jnlpFile.getResources( ) = " + jnlpFile.getResources() );
        System.out.println( "jnlpFile.getApplication() = " + jnlpFile.getApplication() );
        System.out.println( "jnlpFile.getFileLocation() = " + jnlpFile.getFileLocation() );
        System.out.println( "jnlpFile.getTitle() = " + jnlpFile.getTitle() );
    }
}
