// Copyright 2002-2011, University of Colorado
package jass.contact;

import jass.engine.BufferNotAvailableException;
import jass.engine.Out;
import jass.engine.SinkIsFullException;
import jass.generators.Butter2LowFilter;
import jass.generators.FilterContainer;
import jass.generators.LoopBuffer;
import jass.generators.LoopNBuffers;

/**
 * A force model with impact, slide, and slide modes based on looping wav files.
 * Roll force is fed through lowpass filter.
 * It allows for N scraping textures, represented by N wav files. You can set which
 * wav file you want to use for scraping, or any linear superposition of the N wav files.
 * Since rolling still has only one wav file this generator is really useful only for
 * scraping at present.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ContactForceN extends Out {

    // Has these protected sources:
    protected BangForce bangForce;
    protected LoopNBuffers slideForce;
    protected int nSlideForces = 1;
    protected LoopBuffer rollForceRaw;
    protected FilterContainer rollForce;
    // lowpass filter for rolling
    protected Butter2LowFilter lowPassFilter;
    // Cutoff frequency of lowpas filter in Hertz
    protected float fLowPass = 100.f;
    protected float dryRollGain = 0; // 1 for raw wav force, 0 for pure filtered

    private float[] tmpSpeed, tmpForce; // temp vars

    // properties defining how physical parameters are mapped to audio parameters
    // [x0,x1] is range of x

    // maximum audio speeds, 1 is original wav file
    protected float slideSpeed1 = 1f, rollSpeed1 = 1f;

    // physical speed ranges
    protected float vslide0 = .1f, vslide1 = 1f, vroll0 = .1f, vroll1 = 1f;

    protected float[] slideBalance; // determines gains of components of slide force

    // gain ratios
    protected float physicalToAudioGainSlide = 1, physicalToAudioGainRoll = 1, physicalToAudioGainImpact = 1;

    /**
     * Set model parameters mapping physical units to audio units
     *
     * @param slideSpeed1               maximum audio loop speed (1 = original recording)
     * @param rollSpeed1                maximum audio loop speed (1 = original recording)
     * @param vslide0                   minimum physical speed (lower than this is considered to be zero)
     * @param vslide1                   maximum physical speed (higher than this is set to this value)
     * @param vroll0                    minimum physical speed (lower than this is considered to be zero)
     * @param vroll1                    maximum physical speed (higher than this is set to this value)
     * @param physicalToAudioGainSlide  multiplies normal force to get slide gain
     * @param physicalToAudioGainRoll   multiplies normal force to get roll gain
     * @param physicalToAudioGainImpact multiplies impact force to get impact gain
     */
    public void setStaticContactModelParameters( float slideSpeed1, float rollSpeed1, float vslide0,
                                                 float vslide1, float vroll0, float vroll1,
                                                 float physicalToAudioGainSlide,
                                                 float physicalToAudioGainRoll, float physicalToAudioGainImpact ) {
        this.slideSpeed1 = slideSpeed1;
        this.rollSpeed1 = rollSpeed1;
        this.vslide0 = vslide0;
        this.vslide1 = vslide1;
        this.vroll0 = vroll0;
        this.vroll1 = vroll1;
        this.physicalToAudioGainSlide = physicalToAudioGainSlide;
        this.physicalToAudioGainRoll = physicalToAudioGainRoll;
        this.physicalToAudioGainImpact = physicalToAudioGainImpact;
    }

    /**
     * Contructor intended only for subclass constructors (super(bufferSize);)
     */
    protected ContactForceN( int bufferSize ) {
        super( bufferSize );
    }

    /**
     * Construct contact force from named files.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param fnImpact   Audio file name for impact. (For example cos20ms.wav.)
     * @param fnSlide    Audio file names for slide. (For example grid.wav.)
     * @param fnRoll     Audio file name for slide. (For example roll.wav.)
     */
    public ContactForceN( float srate, int bufferSize, String fnImpact, String[] fnSlide, String fnRoll ) {
        super( bufferSize );
        bangForce = new BangForce( srate, bufferSize, fnImpact );
        slideForce = new LoopNBuffers( srate, bufferSize, fnSlide );
        nSlideForces = fnSlide.length;
        rollForceRaw = new LoopBuffer( srate, bufferSize, fnRoll );
        bangForce.setTime( getTime() );
        slideForce.setTime( getTime() );
        rollForceRaw.setTime( getTime() );
        lowPassFilter = new Butter2LowFilter( srate );
        lowPassFilter.setCutoffFrequency( fLowPass );
        rollForce = new FilterContainer( srate, bufferSize, lowPassFilter );
        try {
            rollForce.addSource( rollForceRaw );
        }
        catch( SinkIsFullException e ) {
            System.out.println( this + " " + e );
        }
        rollForce.setTime( getTime() );
        tmpSpeed = new float[nSlideForces];
        tmpForce = new float[nSlideForces];
        slideBalance = new float[nSlideForces];
        slideBalance[0] = 1f;
        for( int i = 1; i < nSlideForces; i++ ) {
            slideBalance[i] = 1f;
        }
    }

    /**
     * Set balance verctor for slide force components
     *
     * @param bal vector of balances
     */
    public synchronized void setSlideBalance( float[] bal ) {
        for( int i = 0; i < nSlideForces; i++ ) {
            slideBalance[i] = bal[i];
        }
    }

    /**
     * get balance
     *
     * @return array of balances
     */
    public float[] getSlideBalance() {
        return slideBalance;
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
     * Set slide speed and normal force  in physical units.
     *
     * @param force normal force.
     * @param speed relative surface velocity.
     */
    public synchronized void setSlideProperties( float force, float speed ) {
        if( speed > vslide1 ) {
            speed = vslide1;
        }
        else if( speed < vslide0 ) {
            speed = 0;
            force = 0;
        }
        float vol = (float)( Math.sqrt( force * speed / vslide1 ) );
        for( int i = 0; i < nSlideForces; i++ ) {
            tmpSpeed[i] = speed * slideSpeed1 / vslide1;
            tmpForce[i] = physicalToAudioGainSlide * vol * slideBalance[i];
        }
        slideForce.setSpeed( tmpSpeed );
        //System.out.println("f0= "+tmpForce[0]+" f1= "+tmpForce[1]);
        slideForce.setVolume( tmpForce );
    }

    /**
     * Set roll speed and normal force in physical units.
     *
     * @param force normal force.
     * @param speed roll velocity.
     */
    public synchronized void setRollProperties( float force, float speed ) {
        if( speed > vroll1 ) {
            speed = vroll1;
        }
        else if( speed < vroll0 ) {
            speed = 0;
            force = 0;
        }
        rollForceRaw.setSpeed( speed * rollSpeed1 / vroll1 );
        float vol = (float)( Math.sqrt( force * speed / vroll1 ) );
        rollForceRaw.setVolume( physicalToAudioGainRoll * vol );
    }


    /**
     * Set roll force filter properties: cutoff and drygain.
     */
    public synchronized void setRollFilter( float fLowPass, float dryRollGain ) {
        this.fLowPass = fLowPass;
        lowPassFilter.setCutoffFrequency( fLowPass );
        this.dryRollGain = dryRollGain;
    }

    /**
     * Compute the next buffer.
     */
    public synchronized void computeBuffer() {
        try {
            float[] b1 = bangForce.getBuffer( getTime() );
            float[] b2 = slideForce.getBuffer( getTime() );
            float[] b3 = rollForceRaw.getBuffer( getTime() );
            float[] b4 = rollForce.getBuffer( getTime() );
            int bufsz = getBufferSize();
            for( int k = 0; k < bufsz; k++ ) {
                buf[k] = b1[k] + b2[k] + dryRollGain * b3[k] + ( 1 - dryRollGain ) * b4[k];
            }
        }
        catch( BufferNotAvailableException e ) {
            System.out.println( this + " " + e );
        }
    }
}


