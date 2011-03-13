// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

/**
 * @author Sam Reid
 */
public class Sample implements Serializable {
    private @Id ObjectId id = new ObjectId();//TODO: why needed?
    private long time;//server time
    private @Indexed SessionID sessionID;
    private Object data;
    private @Indexed long index;
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
