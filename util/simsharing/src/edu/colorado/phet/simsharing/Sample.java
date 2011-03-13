// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class Sample implements Serializable {
    private long time;//server time
    private SessionID sessionID;
    private Object data;
    private long index;
    private int totalSampleCount;

    public Sample( long time, SessionID sessionID, Object data, int index, int totalSampleCount ) {
        this.time = time;
        this.sessionID = sessionID;
        this.data = data;
        this.index = index;
        this.totalSampleCount = totalSampleCount;
    }

    public Sample() {
    }

    public Object getData() {
        return data;
    }

    public long getTime() {
        return time;
    }

    public int getTotalSampleCount() {
        return totalSampleCount;
    }
}
