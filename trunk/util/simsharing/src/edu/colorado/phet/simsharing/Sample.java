// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class Sample implements Serializable {
    long time;//server time
    private final Object data;

    public Sample( long time, Object data ) {
        this.time = time;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public long getTime() {
        return time;
    }
}
