// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.messages;

import java.io.Serializable;

/**
 * Request to obtain samples after the specified time
 *
 * @author Sam Reid
 */
public class GetSamplesAfter implements Serializable {
    public final SessionID id;
    public final long time;

    public GetSamplesAfter( SessionID id, long time ) {
        this.id = id;
        this.time = time;
    }
}