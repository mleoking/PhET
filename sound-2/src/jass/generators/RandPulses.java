package jass.generators;

import jass.engine.Out;

/**
 Output random pulses  uniform in range [-1 +1]. Probability per sample
 is pps. Amplitude is rnd^exponent.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public class RandPulses extends Out {
    /** Probability per sample of pulse */
    protected float pps = 0;
    protected double exponent = 1;

    public RandPulses(int bufferSize) {
        super(bufferSize);
    }

    /** Set impulse probability per sample
     @param pps probability per sample
     */
    public void setProbabilityPerSample(float pps) {
        this.pps = pps;
    }


    /** Set impulse prob. exponent. Volume of impact is r^exponent, with
     r uniform on [0 1]
     @param exponent exponent of prob. distribution
     */
    public void setProbabilityDistributionExponent(float exponent) {
        this.exponent = (double) exponent;
    }

    protected void computeBuffer() {
        int bufsz = getBufferSize();
        for (int i = 0; i < bufsz; i++) {
            double x = Math.random();
            if (x < (double) pps) {
                double vol = 2 * Math.random() - 1;
                int sign;
                if (vol < 0) {
                    sign = -1;
                    vol = -vol;
                } else {
                    sign = 1;
                }
                vol = Math.pow(vol, exponent);
                buf[i] = (float) (vol * sign);
            } else {
                buf[i] = 0;
            }
        }
    }

}

