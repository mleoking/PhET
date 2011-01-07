// Copyright 2002-2011, University of Colorado
package jass.engine;

import java.util.Vector;

/**
 * Input unit which contains Sources and does something with them in
 * run().  Maintains Vector of Sources. Needs only implementation of run().
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public abstract class In extends Thread implements Sink {
    protected Vector sourceContainer;

    public In() {
        sourceContainer = new Vector();
    }

    /**
     * add source to Sink.
     *
     * @param s Source to add.
     * @return object representing Source in Sink (may be null).
     */
    public Object addSource( Source s ) throws SinkIsFullException {
        sourceContainer.addElement( s );
        return null;
    }

    /**
     * Remove Source.
     *
     * @param s Source to remove.
     */
    public void removeSource( Source s ) {
        sourceContainer.removeElement( s );
    }

    /**
     * Get array of sources.
     *
     * @return array of the Sources, null if there are none.
     */
    public Object[] getSources() {
        return sourceContainer.toArray();
    }
}

