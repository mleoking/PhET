package jass.generators;

import jass.engine.Out;

/**
 A force model based on looping through N buffers, loaded from audio
 files or provided by caller. Speed can be set as an N-vector. Volume is same.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class LoopNBuffers extends Out {
    /** Number of buffers. */
    protected int nbuffers;

    /** Contains nbuffers looping buffers */
    protected LoopBuffer[] lb;

    /** Construct loop forces from named files.
     @param srate sampling rate in Hertz.
     @param bufferSize bufferSize of this Out
     @param fn Audio file names.
     */
    public LoopNBuffers(float srate, int bufferSize, String[] fn) {
        super(bufferSize); // this is the internal buffer size
        nbuffers = fn.length;
        lb = new LoopBuffer[nbuffers];
        for (int i = 0; i < nbuffers; i++) {
            lb[i] = new LoopBuffer(srate, bufferSize, fn[i]);
        }
    }

    /** Set force magnitude.
     @param val Volume.
     */
    public void setVolume(float[] val) {
        for (int i = 0; i < nbuffers; i++) {
            lb[i].setVolume(val[i]);
            //System.out.println("bufVol0= "+val[0]+" bufVol1= "+val[1]);
        }
    }

    /** Set loopspeed.
     @param speed Loop speeds, 1 corresponding to original recorded speed.
     */
    public void setSpeed(float[] speed) {
        for (int i = 0; i < nbuffers; i++) {
            lb[i].setSpeed(speed[i]);
        }
    }

    /** Compute the next buffer.
     */
    public void computeBuffer() {
        int bufsz = getBufferSize();
        for (int k = 0; k < bufsz; k++) {
            buf[k] = 0;
        }
        for (int i = 0; i < nbuffers; i++) {
            lb[i].computeBuffer();
            float[] lbBuf = lb[i].peekAtBuffer();
            //if(i==1) {
            //    System.out.println("buf="+i+" "+lbBuf[0]);
            //}
            for (int k = 0; k < bufsz; k++) {
                buf[k] += lbBuf[k];
            }
        }
    }

}


