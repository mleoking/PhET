// Copyright 2002-2011, University of Colorado
package jass.generators;

import jass.engine.InOut;
import jass.engine.SinkIsFullException;
import jass.engine.Source;

/**
 * Filter UG. One input only. Processes input through filter. Output is
 * mix of filtered and dry signal.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class FilterContainer extends InOut {

    /**
     * Add source to Sink. Override to allow only one input.
     *
     * @param s Source to add.
     * @return object representing Source in Sink (may be null).
     */
    public Object addSource( Source s ) throws SinkIsFullException {
        if( sourceContainer.size() > 0 ) {
            throw new SinkIsFullException();
        }
        else {
            sourceContainer.addElement( s );
        }
        return null;
    }

    /**
     * Filter
     */
    Filter filter = null;

    /**
     * Mix.
     */
    protected float dryLevel = 0;

    /**
     * Mix level (default = 0)
     *
     * @param level mix level. 0 is only filtered, 1 is only dry signal
     */
    public void setDryLevel( float level ) {
        dryLevel = level;
    }

    /**
     * Get mix level.
     *
     * @return mix level. 0 is only filtered, 1 is only dry signal
     */
    public float getDrylevel() {
        return dryLevel;
    }

    /**
     * Return Filter object contained.
     *
     * @return Filter contained.
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * SetFilter contained.
     *
     * @param f Filter contained.
     */
    public void setFilter( Filter f ) {
        filter = f;
    }

    /**
     * Create container around Filter.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize Buffer size used for real-time rendering.
     * @param f          Filter contained.
     */
    public FilterContainer( float srate, int bufferSize, Filter f ) {
        super( bufferSize );
        setFilter( f );
    }

    /**
     * Compute the next buffer and store in member float[] buf.
     */
    protected void computeBuffer() {
        float[] tmpbuf = srcBuffers[0];
        int offSet = 0;
        int bufsz = getBufferSize();
        filter.filter( this.buf, tmpbuf, bufsz, offSet );
        // mix dry signal in accordingly
        float wetLevel = 1.f - dryLevel;
        for( int k = 0; k < bufsz; k++ ) {
            buf[k] = wetLevel * buf[k] + dryLevel * tmpbuf[k];
        }
    }

}
