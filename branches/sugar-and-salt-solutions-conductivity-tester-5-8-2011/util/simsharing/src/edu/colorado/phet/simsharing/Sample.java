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
    private String json;
    private @Indexed long index;
    private int totalSampleCount;

    public Sample( long time, SessionID sessionID, String json, long index, int totalSampleCount ) {
        this.time = time;
        this.sessionID = sessionID;
        this.json = json;
        this.index = index;
        this.totalSampleCount = totalSampleCount;
    }

    public Sample() {
    }

    public String getJson() {
        return json;
    }

    public long getTime() {
        return time;
    }

    public int getTotalSampleCount() {
        return totalSampleCount;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public long getIndex() {
        return index;
    }
}
