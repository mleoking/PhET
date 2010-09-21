package jass.generators;


/**
 * Play a buffer once
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ConstantOneShotBuffer extends ConstantLoopBuffer {

    /**
     * True if is outputting buffer.
     */
    private boolean isHit = false;

    /**
     * Construct loop force from named file.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param fn         Audio file name.
     */
    public ConstantOneShotBuffer( float srate, int bufferSize, String fn ) {
        super( srate, bufferSize, fn );
        reset();
    }

    /**
     * Construct loop force and provide buffer.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param loopBuffer looping buffer.
     */
    public ConstantOneShotBuffer( float srate, int bufferSize, float[] loopBuffer ) {
        super( srate, bufferSize, loopBuffer );
        reset();
    }

    /**
     * Calling this method will cause next call to getBuffer() to
     * start playback of the buffer.
     */
    public void hit() {
        isHit = true;
    }

    /**
     * Reset buffer.
     */
    private void reset() {
        isHit = false;
        ix = 0;
    }

    /**
     * Compute the next buffer. If not hit return zero.
     */
    public void computeBuffer() {
        int bufsz = getBufferSize();
        if( !isHit ) {
            for( int k = 0; k < bufsz; k++ ) {
                buf[k] = 0;
            }
        }
        else {
            // fill with buffer until end is reached
            for( int k = 0; k < bufsz; k++ ) {
                buf[k] = loopBuffer[ix];
                if( ix == loopBufferLength - 1 ) { // wrapped around: stop
                    reset(); // done
                    break;
                }
                else {
                    ix++;
                }
            }
        }
    }

}


