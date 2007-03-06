/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: Sam Reid
 * Date: Apr 3, 2006
 * Time: 2:49:52 AM
 * Copyright (c) Apr 3, 2006 by Sam Reid
 */
public class OutputRedirection implements Runnable {
    InputStream in;

    public OutputRedirection( InputStream in ) {
        this.in = in;
    }

    public void run() {
        int c;
        try {
            while( ( c = in.read() ) != -1 ) {
                System.out.write( c );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        try {
            in.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
