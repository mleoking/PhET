package jass.generators;


/**
 A force model based on looping through a buffer once, loaded from an audio
 file or provided by caller.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class OneShotBuffer extends LoopBuffer {

    /** True if is outputting buffer. */
    private boolean isHit = false;

    /** Construct loop force from named file.
     @param srate sampling rate in Hertz.
     @param bufferSize bufferSize of this Out.
     @param fn Audio file name.
     */
    public OneShotBuffer(float srate, int bufferSize, String fn) {
        super(srate, bufferSize, fn);
        reset();
    }

    /** Construct loop force and provide buffer.
     @param srate sampling rate in Hertz.
     @param bufferSize bufferSize of this Out.
     @param loopBuffer looping buffer.
     */
    public OneShotBuffer(float srate, int bufferSize, float[] loopBuffer) {
        super(srate, bufferSize, loopBuffer);
        reset();
    }

    /** Calling this method will cause next call to getBuffer() to
     start playback of the buffer.
     */
    public void hit() {
        isHit = true;
    }

    /** Reset buffer.
     */
    private void reset() {
        isHit = false;
        ix = 0;
        x = 0;
    }

    /** Compute the next buffer. If not hit return zero.
     */
    public void computeBuffer() {
        int bufsz = getBufferSize();
        if (!isHit) {
            for (int k = 0; k < bufsz; k++) {
                buf[k] = 0;
            }
        } else {
            // fill with buffer until end is reached
            for (int k = 0; k < bufsz; k++) {
                int ixnext = ix + dix;
                if (x + dx >= 1) {
                    ixnext++;
                }
                float y = getNextSample();
                if (ixnext >= loopBufferLength) { // wrapped around: stop
                    buf[k] = y;
                    reset(); // done
                    break;
                } else {
                    buf[k] = y;
                }
            }
        }
    }

}


