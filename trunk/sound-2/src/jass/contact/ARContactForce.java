package jass.contact;

import jass.engine.BufferNotAvailableException;
import jass.engine.SinkIsFullException;
import jass.generators.*;

/**
 A force model with impact, slide, and slide modes based on AR2 model for
 sliding, wavetable model as in ContactForce for rolling and impact.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ARContactForce extends ContactForce {
    boolean isComplexReson = false;

    // contains reson filter which is fed white noise
    private FilterContainer slideForce;

    // reson filter for sliding (AR2 model)
    private ResonFilter resonSlideFilter;

    // white noise source
    private ConstantLoopBuffer loopedNoise;

    // freq., damp. and gain of AR model (Reson).
    private float ARd = 1000f,ARf = 100f,ARa = 1f,maxARd = ARd; // max is value at max freq.

    // min and max reson values
    private float slideFreq0 = 10f,slideFreq1 = 2000f;

    /** Constructor intended only for subclass constructors (super(bufferSize);)
     */
    protected ARContactForce(int bufferSize) {
        super(bufferSize);
    }

    /** Construct contact force from named files.
     @param srate sampling rate in Hertz.
     @param bufferSize bufferSize of this Out.
     @param fnImpact Audio file name for impact. (For example cos20ms.wav.)
     @param fnRoll Audio file name for slide. (For example white5.wav.) Will not be pitch shifted but filtered with AR model.
     @param fnRoll Audio file name for slide. (For example roll.wav.)
     */
    public ARContactForce(float srate, int bufferSize, String fnImpact, String fnSlide, String fnRoll) {
        super(bufferSize);
        bangForce = new BangForce(srate, bufferSize, fnImpact);
        rollForceRaw = new LoopBuffer(srate, bufferSize, fnRoll);
        // build slide source as loop buffer feeding into reson filter
        resonSlideFilter = new ResonFilter(srate);
        loopedNoise = new ConstantLoopBuffer(srate, bufferSize, fnSlide);
        slideForce = new FilterContainer(srate, bufferSize, resonSlideFilter);
        if (isComplexReson) {
            resonSlideFilter.setResonCoeff(-ARf, ARd, ARa);
        } else {
            resonSlideFilter.setResonCoeff(ARf, ARd, ARa);
        }
        try {
            slideForce.addSource(loopedNoise);
        } catch (SinkIsFullException e) {
            System.out.println(this + " " + e);
        }

        bangForce.setTime(getTime());
        slideForce.setTime(getTime());
        rollForceRaw.setTime(getTime());
        lowPassFilter = new Butter2LowFilter(srate);
        lowPassFilter.setCutoffFrequency(fLowPass);
        rollForce = new FilterContainer(srate, bufferSize, lowPassFilter);
        try {
            rollForce.addSource(rollForceRaw);
        } catch (SinkIsFullException e) {
            System.out.println(this + " " + e);
        }
        rollForce.setTime(getTime());
    }


    /** Set model parameters mapping physical units to audio units
     @param slideFreq0 minimum reson freq. (should be zero)
     @param slideFreq1 maximum reson freq. (should correspond to max velocity)
     @param rollSpeed1 maximum audio loop speed (1 = original recording)
     @param vslide0 minimum physical speed (lower than this is considered to be zero)
     @param vslide1 maximum physical speed (higher than this is set to this value)
     @param vroll0 minimum physical speed (lower than this is considered to be zero)
     @param vroll1 maximum physical speed (higher than this is set to this value)
     @param physicalToAudioGainSlide multiplies normal force to get slide gain
     @param physicalToAudioGainRoll multiplies normal force to get roll gain
     @param physicalToAudioGainImpact multiplies impact force to get impact gain
     @param boolean isComplexReson if is complex reson (all freq negative)
     */
    public void setStaticContactModelParameters(float slideFreq0, float slideFreq1, float rollSpeed1, float vslide0,
                                                float vslide1, float vroll0, float vroll1,
                                                float physicalToAudioGainSlide,
                                                float physicalToAudioGainRoll, float physicalToAudioGainImpact,
                                                boolean isComplexReson) {
        this.slideFreq0 = slideFreq0;
        this.slideFreq1 = slideFreq1;
        this.rollSpeed1 = rollSpeed1;
        this.vslide0 = vslide0;
        this.vslide1 = vslide1;
        this.vroll0 = vroll0;
        this.vroll1 = vroll1;
        this.physicalToAudioGainSlide = physicalToAudioGainSlide;
        this.physicalToAudioGainRoll = physicalToAudioGainRoll;
        this.physicalToAudioGainImpact = physicalToAudioGainImpact;
        this.isComplexReson = isComplexReson;
    }


    /** Set slide model damping (usually static property).
     This is actually the damping at maximum velocity and scales down
     with the reson freq.
     @param d damping
     */
    public void setSlideModelDamping(float d) {
        this.maxARd = d;
    }

    /** Set slide speed and normal force.
     @param force normal force.
     @param speed relative surface velocity. (Audio file is speed = 1.)
     */
    public synchronized void setSlideProperties(float force, float speed) {
        // f= a*v +b;
        float a = (slideFreq1 - slideFreq0) / (vslide1 - vslide0);
        float b = slideFreq0 - a * vslide0;
        float f,d; // freq and gain

        float vol = (float) (Math.sqrt(force * speed / vslide1));
        if (speed > vslide1) {
            ARd = maxARd;
            speed = vslide1;
            ARf = slideFreq1;
            ARa = physicalToAudioGainSlide * vol;
        } else if (speed < vslide0) {
            ARf = slideFreq1;
            ARa = 0;
            ARd = maxARd;
        } else {
            ARf = a * speed + b;
            ARd = maxARd * speed / vslide1;
            ARa = physicalToAudioGainSlide * vol;
        }
        if (isComplexReson) {
            resonSlideFilter.setResonCoeff(-ARf, ARd, ARa);
        } else {
            resonSlideFilter.setResonCoeff(ARf, ARd, ARa);
        }
    }

    /** Compute the next buffer.
     */
    public synchronized void computeBuffer() {
        try {
            float[] b1 = bangForce.getBuffer(getTime());
            float[] b2 = slideForce.getBuffer(getTime());
            float[] b3 = rollForceRaw.getBuffer(getTime());
            float[] b4 = rollForce.getBuffer(getTime());
            int bufsz = getBufferSize();
            for (int k = 0; k < bufsz; k++) {
                buf[k] = b1[k] + b2[k] + dryRollGain * b3[k] + (1 - dryRollGain) * b4[k];
            }
        } catch (BufferNotAvailableException e) {
            System.out.println(this + " " + e);
        }
    }

}








