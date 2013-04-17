// Copyright 2002-2011, University of Colorado
package jass.contact;

import jass.engine.BufferNotAvailableException;
import jass.engine.Out;
import jass.engine.SinkIsFullException;
import jass.generators.*;

/**
 * A force model with impact, slide, and slide modes based on looping wav files,
 * fed through Reson.
 * Roll force is fed through modal filter.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class FilteredWavContactForce extends Out {

    // impact
    protected BangForce bangForce;

    // slide
    protected ConstantLoopBuffer slideForceRaw;
    protected FilterContainer slideForce;
    protected ResonFilter resonSlideFilter;
    // freq., damp. and gain of AR model (Reson).
    protected float slideARd = 1000f, slideARf = 100f, slideARa = 1f;
    // min and max reson values
    protected float slideFreq0 = 10f, slideFreq1 = 2000f;
    // physical speed range
    protected float vslide0 = 0, vslide1 = 1f;

    // roll
    protected ConstantLoopBuffer rollForceRaw;
    protected FilterContainer rollReson; // reson container
    protected ResonFilter resonRollFilter;
    // freq., damp. and gain of AR model (Reson).
    protected float rollARd = 1000f, rollARf = 100f, rollARa = 1f;
    // min and max reson values
    protected float rollFreq0 = 10f, rollFreq1 = 2000f;
    // modal filter for rolling
    protected ModalObjectWithOneContact rollForce;
    // Damping scale of roll filter
    protected float rollModalDamping = 1.f;
    // physical speed range
    protected float vroll0 = 0, vroll1 = 1f;

    // gain ratios
    protected float physicalToAudioGainSlide = 1,
            physicalToAudioGainRoll = 1,
            physicalToAudioGainImpact = 1;

    /**
     * Set model parameters mapping physical units to audio units
     *
     * @param slideFreq0                minimum reson freq.
     * @param slideFreq1                maximum reson freq.
     * @param rollFreq0                 minimum reson freq.
     * @param rollFreq1                 maximum reson freq.
     * @param vslide0                   minimum physical speed (lower than this is considered to be zero)
     * @param vslide1                   maximum physical speed (higher than this is set to this value)
     * @param vroll0                    minimum physical speed (lower than this is considered to be zero)
     * @param vroll1                    maximum physical speed (higher than this is set to this value)
     * @param physicalToAudioGainSlide  multiplies normal force to get slide gain
     * @param physicalToAudioGainRoll   multiplies normal force to get roll gain
     * @param physicalToAudioGainImpact multiplies impact force to get impact gain
     */
    public void setStaticContactModelParameters( float slideFreq0, float slideFreq1,
                                                 float rollFreq0, float rollFreq1,
                                                 float vslide0, float vslide1,
                                                 float vroll0, float vroll1,
                                                 float physicalToAudioGainSlide,
                                                 float physicalToAudioGainRoll,
                                                 float physicalToAudioGainImpact ) {
        this.slideFreq0 = slideFreq0;
        this.slideFreq1 = slideFreq1;
        this.rollFreq0 = rollFreq0;
        this.rollFreq1 = rollFreq1;
        this.vslide0 = vslide0;
        this.vslide1 = vslide1;
        this.vroll0 = vroll0;
        this.vroll1 = vroll1;
        this.physicalToAudioGainSlide = physicalToAudioGainSlide;
        this.physicalToAudioGainRoll = physicalToAudioGainRoll;
        this.physicalToAudioGainImpact = physicalToAudioGainImpact;
    }


    /**
     * Constructor intended only for subclass constructors (super(bufferSize);)
     */
    protected FilteredWavContactForce( int bufferSize ) {
        super( bufferSize );
    }

    /**
     * Construct contact force from named files.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param fnImpact   Audio file name for impact. (For example cos20ms.wav.)
     * @param fnSlide    Audio file name for slide. (For example grid.wav.)
     * @param fnRoll     Audio file name for slide. (For example roll.wav.)
     * @param mm         ModalModel
     */
    public FilteredWavContactForce( float srate, int bufferSize,
                                    String fnImpact, String fnSlide,
                                    String fnRoll, ModalModel mm ) {
        super( bufferSize );
        // impact
        bangForce = new BangForce( srate, bufferSize, fnImpact );

        // slide filtergraph
        slideForceRaw = new ConstantLoopBuffer( srate, bufferSize, fnSlide );
        resonSlideFilter = new ResonFilter( srate );
        resonSlideFilter.setResonCoeff( slideARf, slideARd, slideARa );
        slideForce = new FilterContainer( srate, bufferSize, resonSlideFilter );
        try {
            slideForce.addSource( slideForceRaw );
        }
        catch( SinkIsFullException e ) {
            System.out.println( this + " 1" + e );
        }

        // roll filtergraph
        rollForceRaw = new ConstantLoopBuffer( srate, bufferSize, fnRoll );
        resonRollFilter = new ResonFilter( srate );
        resonRollFilter.setResonCoeff( rollARf, rollARd, rollARa );
        rollReson = new FilterContainer( srate, bufferSize, resonRollFilter );
        rollForce = new ModalObjectWithOneContact( mm, srate, bufferSize );
        rollForce.setDamping( rollModalDamping );
        try {
            rollForce.addSource( rollReson );
        }
        catch( SinkIsFullException e ) {
            System.out.println( this + " 2" + e );
        }
        try {
            rollReson.addSource( rollForceRaw );
        }
        catch( SinkIsFullException e ) {
            System.out.println( this + " 3" + e );
        }
        // synchronize
        bangForce.setTime( getTime() );
        slideForceRaw.setTime( getTime() );
        slideForce.setTime( getTime() );
        rollForceRaw.setTime( getTime() );
        rollReson.setTime( getTime() );
        rollForce.setTime( getTime() );
    }

    /**
     * Set slide model damping (usually static property)
     *
     * @param d damping
     */
    public void setSlideModelDamping( float d ) {
        this.slideARd = d;
    }

    /**
     * Set slide reson at low lvel
     */
    public void setSlideReson( float f, float d, float a ) {
        if( f < 1f ) {
            f = .1f;
            a = 0;
        }
        slideARf = f;
        slideARd = d;
        slideARa = a;
        resonSlideFilter.setResonCoeff( slideARf, slideARd, slideARa );
    }

    /**
     * Set slide speed and normal force  in physical units.
     *
     * @param force normal force.
     * @param speed relative surface velocity.
     */

    public synchronized void setSlideProperties( float force, float speed ) {
        // f= a*v +b;
        float a = ( slideFreq1 - slideFreq0 ) / ( vslide1 - vslide0 );
        float b = slideFreq0 - a * vslide0;
        float f, d; // freq and gain
        float vol = (float)( Math.sqrt( force * speed / vslide1 ) );
        if( speed > vslide1 ) {
            speed = vslide1;
            slideARf = slideFreq1;
            slideARa = physicalToAudioGainSlide * vol;
        }
        else if( speed < vslide0 ) {
            slideARf = slideFreq1;
            slideARa = 0;
        }
        else {
            slideARf = a * speed + b;
            slideARa = physicalToAudioGainSlide * vol;
        }
        resonSlideFilter.setResonCoeff( slideARf, slideARd, slideARa );
    }


    /**
     * Set roll model damping (usually static property)
     *
     * @param d damping
     */
    public void setRollModelDamping( float d ) {
        this.rollARd = d;
    }


    /**
     * Set roll reson at low lvel
     */
    public void setRollReson( float f, float d, float a ) {
        if( f < 1f ) {
            f = .1f;
            a = 0;
        }
        rollARf = f;
        rollARd = d;
        rollARa = a;
        if( f < .1f ) {
            f = .1f;
        }
        resonRollFilter.setResonCoeff( rollARf, rollARd, rollARa );
    }

    /**
     * Set roll speed and normal force in physical units.
     *
     * @param force normal force.
     * @param speed roll velocity.
     */
    public synchronized void setRollProperties( float force, float speed ) {
        // f= a*v +b;
        float a = ( rollFreq1 - rollFreq0 ) / ( vroll1 - vroll0 );
        float b = rollFreq0 - a * vroll0;
        float f, d; // freq and gain
        float vol = (float)( Math.sqrt( force * speed / vroll1 ) );
        if( speed > vroll1 ) {
            speed = vroll1;
            rollARf = rollFreq1;
            rollARa = physicalToAudioGainRoll * vol;
        }
        else if( speed < vroll0 ) {
            rollARf = rollFreq1;
            rollARa = 0;
        }
        else {
            rollARf = a * speed + b;
            rollARa = physicalToAudioGainRoll * vol;
        }
        //System.out.println("roll fda="+rollARf+ " "+rollARd+" "+rollARa);
        resonRollFilter.setResonCoeff( rollARf, rollARd, rollARa );
    }


    /**
     * Set roll force modal filter properties: damping.
     */
    public synchronized void setRollFilterModalDamping( float val ) {
        rollModalDamping = val;
        rollForce.setDamping( rollModalDamping );
    }


    /**
     * Generate impact force  in physical units.
     *
     * @param force magnitude.
     * @param dur   duration in seconds of impact.
     */
    public synchronized void bang( float force, float dur ) {
        bangForce.bang( physicalToAudioGainImpact * force, dur );
    }


    /**
     * Compute the next buffer. Mix down the 3 contributions
     */
    public synchronized void computeBuffer() {
        try {
            float[] b1 = bangForce.getBuffer( getTime() );
            float[] b2 = slideForce.getBuffer( getTime() );
            float[] b3 = rollForce.getBuffer( getTime() );
            int bufsz = getBufferSize();
            for( int k = 0; k < bufsz; k++ ) {
                buf[k] = b1[k] + b2[k] + b3[k];
            }
        }
        catch( BufferNotAvailableException e ) {
            System.out.println( this + " " + e );
        }
    }
}


