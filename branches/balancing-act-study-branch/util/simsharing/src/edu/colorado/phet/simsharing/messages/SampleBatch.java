// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.simsharing.state.SimState;

/**
 * A batch of states retrieved from the server to be played in SimView
 *
 * @author Sam Reid
 */
public class SampleBatch<T extends SimState> implements Serializable, Iterable<T> {
    public final ArrayList<T> states;
    public final int totalNumberStates;

    public SampleBatch( ArrayList<T> states, int totalNumberStates ) {
        this.totalNumberStates = totalNumberStates;
        this.states = new ArrayList<T>( states );
    }

    public Iterator<T> iterator() {
        return states.iterator();
    }
}