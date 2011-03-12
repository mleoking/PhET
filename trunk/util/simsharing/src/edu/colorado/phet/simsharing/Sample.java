// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class Sample implements Serializable {
    private long time;//server time
    private StudentID studentID;
    private Object data;
    private long index;

    public Sample( long time, StudentID studentID, Object data, int index ) {
        this.time = time;
        this.studentID = studentID;
        this.data = data;
        this.index = index;
    }

    public Sample() {
    }

    public Object getData() {
        return data;
    }

    public long getTime() {
        return time;
    }
}
