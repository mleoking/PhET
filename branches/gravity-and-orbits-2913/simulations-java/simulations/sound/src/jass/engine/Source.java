// Copyright 2002-2011, University of Colorado
package jass.engine;

/**
 * Interface defining a source. This produces audio-rate buffers. Has
 * buffer with audio data and a time concept .
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public interface Source {
    /**
     * Get buffer with timestamp t.
     *
     * @param t timestamp of buffer. For example, a frame index.
     */
    float[] getBuffer( long t ) throws BufferNotAvailableException;

    /**
     * Get current time.
     *
     * @return current time.
     */
    long getTime();

    /**
     * Set current time.
     *
     * @param t current time.
     */
    void setTime( long t );

    /**
     * Get buffer size.
     *
     * @return buffer size in samples.
     */
    int getBufferSize();

    /**
     * Set buffer size.
     *
     * @param bufferSize buffer size.
     */
    void setBufferSize( int bufferSize );

    /**
     * Clears buffer to zero.
     */
    void clearBuffer();
}

