package jass.engine;

import java.util.Vector;

/**
 Input/output unit. Needs only implementation of computeBuffer().
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public abstract class InOut extends Out implements Sink {

    protected Vector sourceContainer;

    /** add source to Sink.
     @param s Source to add.
     @return object representing Source in Sink (may be null).
     */
    public synchronized Object addSource(Source s) throws SinkIsFullException {
        sourceContainer.addElement(s);
        s.setTime(getTime());
        return null;
    }

    public synchronized void removeSource(Source s) {
        sourceContainer.removeElement(s);
    }

    /** Get array of sources.
     @return array of the Sources, null if there are none.
     */
    public Object[] getSources() {
        return sourceContainer.toArray();
    }

    /**
     Array of buffers of the sources
     */
    protected float[][] srcBuffers;

    public InOut(int bufferSize) {
        super(bufferSize);
        sourceContainer = new Vector();
        srcBuffers = new float[1][];
    }

    /** Call all the sources and cache their returned buffers.
     */
    private final void callSources() {
        Object[] src = getSources();
        int n = src.length; // number of sources
        int n_buf = srcBuffers.length; // number of source buffers allocated

        if (n_buf < n) {
            srcBuffers = new float[n][];
        }
        try {
            for (int i = 0; i < n; i++) {
                srcBuffers[i] = ((Source) src[i]).getBuffer(getTime());
            }
        } catch (BufferNotAvailableException e) {
            System.out.println("InOut.callSources: " + this + " " + e);
        }
    }

    /**
     Get buffer with frame index t. Return old buffer if have it in cache.
     Compute next buffer and advance time if requested, throw exception if
     requested buffer lies in the past or future.  This method will be
     calle "behind the scenes" when processing filtergraphs.
     @param t timestamp of buffer = frame index.
     */
    public synchronized float[] getBuffer(long t) throws BufferNotAvailableException {
        if (t == getTime() + 1) { // requested next buffer
            setTime(t);
            callSources();
            computeBuffer(); // use cached source buffers to compute buf.
        } else if (t != getTime()) { // neither current or next buffer requested: deny request
            System.out.println("Error! " + this + " Out.java: t=" + t + " currentTime=" + getTime());
            throw new BufferNotAvailableException();
        }
        // return new or old buffer:
        return buf;
    }
}

