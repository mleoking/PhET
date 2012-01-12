// Copyright 2002-2011, University of Colorado
package jass.generators;


/**
 * A force model based on stick slip model. Just load a period of sawtooth.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class StickSlipSimple extends Impulse {

    private float srate; // sampling rate in Hertz
    private float v_crit; // critical velocity: no stick slip above this
    private float k_over_mu = 1; // spring const/coeff. of friction
    private float fn_min; // min, normal force: no stick slip if below this

    private float v = 1; // external slide velocity in m/s
    private float fn = 1; // external normal force in Newton

    /**
     * Construct force.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     */
    public StickSlipSimple( float srate, int bufferSize ) {
        super( srate, bufferSize );
        reset();
    }

    /**
     * Set model pars
     *
     * @param k_over_mu k/mu
     * @param fmin      min. norm. force below which no output
     * @param vcrit     max speed above which no output
     */
    public void setModelParameters( float k_over_mu, float fmin, float vcrit ) {
        this.k_over_mu = k_over_mu;
        this.fn_min = fmin;
        this.v_crit = vcrit;
    }

    /**
     * Set velocity and force
     *
     * @param v velocity in m/s
     */
    public void setContactProperties( float vel, float force ) {
        // freq. is k_over_mu * v/fn
        if( vel > v_crit || force < fn_min ) { // no sound
            v = 0;
            fn = 0;
            super.setVolume( 0 );
        }
        else {
            v = vel;
            fn = force;
            float period = fn / ( k_over_mu * v );
            super.setVolume( fn );
            super.setPeriod( period );
        }
    }

}




