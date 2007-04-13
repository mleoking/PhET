/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.sound.model;

import edu.colorado.phet.common_sound.model.clock.SwingTimerClock;

import java.util.ArrayList;

/**
 * SoundClock
 * <p>
 * A clock class for the Sound simulation. We need this because the clock in the version of
 * PhetCommon on which this simulation is built doesn't seem to send out state changed events
 * when the clock is unpaused. So this clock class provides that facility. This is needed to
 * get the audio to turn off if the simulation is paused.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SoundClock extends SwingTimerClock {
    ArrayList listeners = new ArrayList();

    public SoundClock( double v, int i ) {
        super( v, i );
    }

    public void setPaused( boolean b ) {
        super.setPaused( b );
        for( int i = 0; i < listeners.size(); i++ ) {
            PauseListener listener = (PauseListener)listeners.get( i );
            listener.stateChanged( b );
        }
    }

    public void addPauseListener( PauseListener listener ) {
        listeners.add( listener );
    }

    public interface PauseListener {
        void stateChanged( boolean isPaused );
    }
}

