// Copyright 2002-2011, University of Colorado
package jass.generators;

import jass.engine.Out;

/**
 * Output white noise wih amplitude [-1 +1]
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public class RandOut extends Out {

    public RandOut( int bufferSize ) {
        super( bufferSize );
    }

    protected void computeBuffer() {
        int bufsz = getBufferSize();
        for( int i = 0; i < bufsz; i++ ) {
            double x = Math.random();
            buf[i] = (float)( 2 * x - 1 );
        }
    }

}

