// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.SerializableBufferedImage;
import edu.colorado.phet.common.phetcommon.simsharing.SimState;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.StudentSummary;

/**
 * @author Sam Reid
 */
public class Session<T extends SimState> {
    private long startTime;
    private Option<Long> endTime = new Option.None<Long>();
    private final ArrayList<T> samples = new ArrayList();
    private final SessionID sessionID;

    public Session( SessionID sessionID ) {
        this.sessionID = sessionID;
        startTime = System.currentTimeMillis();
    }

    public void endSession() {
        endTime = new Option.Some<Long>( System.currentTimeMillis() );
    }

    public StudentSummary getStudentSummary() {
        SerializableBufferedImage image = samples.size() == 0 ? new SerializableBufferedImage( new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB ) ) : samples.get( samples.size() - 1 ).getImage();
        long time = samples.size() == 0 ? -1 : System.currentTimeMillis() - samples.get( samples.size() - 1 ).getTime();
        return new StudentSummary( sessionID, image, System.currentTimeMillis() - startTime, time, samples.size() );
    }

    public void addSamples( AddSamples<T> sampleSet ) {
        for ( T sample : sampleSet.getData() ) {
            samples.add( sample );
        }
    }

    public T getSample( int index ) {

        //Handle flag for request for latest data point
        if ( index == -1 || index >= samples.size() ) {
            return samples.get( samples.size() - 1 );
        }
        else {
            return samples.get( index );
        }
    }

    public boolean isActive() {
        return endTime.isNone();
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public long getStartTime() {
        return startTime;
    }

    public Integer getNumSamples() {
        return samples.size();
    }
}