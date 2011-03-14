// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.simsharing.SerializableBufferedImage;

/**
 * @author Sam Reid
 */
public class StudentSummary implements Serializable {
    private final SessionID sessionID;
    private final SerializableBufferedImage image;
    private final long upTime;
    private final long timeSinceLastEvent;

    public StudentSummary( SessionID sessionID, SerializableBufferedImage image, long upTime, long timeSinceLastEvent ) {
        this.sessionID = sessionID;
        this.image = image;
        this.upTime = upTime;
        this.timeSinceLastEvent = timeSinceLastEvent;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public BufferedImage getBufferedImage() {
        return image == null ? null : image.getBufferedImage();
    }

    public long getUpTime() {
        return upTime;
    }

    public long getTimeSinceLastEvent() {
        return timeSinceLastEvent;
    }
}
