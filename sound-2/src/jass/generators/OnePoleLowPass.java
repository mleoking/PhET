package jass.generators;

import jass.engine.FilterUG;

/** OnePoleLowPass filter Y = H(z) X.
 H(z) = 1/(1 - g/z)
 y(t) = x(t) + g*y(t-1)
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public class OnePoleLowPass extends FilterUG {

    protected float yt_1;
    protected float g = 0; // filter parameter

    /** Create. For derived classes.
     @param bufferSize Buffer size used for real-time rendering.
     */
    public OnePoleLowPass(int bufferSize) {
        super(bufferSize);
        reset();
    }

    /** Reset state.
     */
    protected void reset() {
        yt_1 = 0;
    }

    /** Set filter parameter
     @param g filter param g
     */
    public void setG(float g) {
        this.g = g;
    }

    /** Get filter parameter
     @return filter param g
     */
    public float getG() {
        return g;
    }

    /** Compute the next buffer and store in member float[] buf.
     */
    protected void computeBuffer() {
        int n = getBufferSize();
        float[] srcBuf = srcBuffers[0];
        for (int i = 0; i < n; i++) {
            yt_1 = srcBuf[i] + g * yt_1;
            buf[i] = yt_1;
        }
    }
}
