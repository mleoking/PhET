package jass.contact;

import jass.engine.Out;

/**
 * A force model based on stick slip model.
 * Karnopp model.
 * <p/>
 * Model is in terms of q(t):
 * <p/>
 * Md^2q/dt^2 + k_d dq/dt + k_s q(t) = G(dq/dt)  FOR |v-dq/dt| > vc
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class KarnoppFriction extends Out {

    private float srate; // sampling rate in Hertz
    private float k_damp; // damping of model
    private float k_spring; // spring constant of model
    private float mu_static; // static (Stribeck) friction coeff.
    private float mu_dynamic; // dynamic  friction coeff.
    private float v_crit; // critical velocity of model
    private float mass; // masss of effective model

    private double qt_1, rt_1; // last state
    private float v = 1; // external slide velocity in m/s
    private float fn = 1; // external normal force in Newton

    /**
     * Construct force.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     */
    public KarnoppFriction( float srate, int bufferSize ) {
        super( bufferSize );
        this.srate = srate;
        reset();
    }

    /**
     * Set stick-slip model parameters (all in S.I. units).
     *
     * @param k_damp     damping of spring system
     * @param mu_static  static friction coeff
     * @param mu_dynamic dynamic friction coeff
     * @param v_crit     critical velocity for transition from dyn. to static friction
     */
    public void setStickSlipParameters( float mass, float k_damp, float k_spring, float mu_static, float mu_dynamic, float v_crit ) {
        this.k_damp = k_damp;
        this.k_spring = k_spring;
        this.mu_static = mu_static;
        this.mu_dynamic = mu_dynamic;
        this.v_crit = v_crit;
        this.mass = mass;
    }

    /**
     * Set normal force magnitude.
     *
     * @param val normal force in Newton
     */
    public void setNormalForce( float val ) {
        fn = val;
    }

    /**
     * Set velocity.
     *
     * @param v velocity in m/s
     */
    public void setSpeed( float v ) {
        this.v = v;
    }

    /**
     * Reset state.
     */
    public void reset() {
        qt_1 = rt_1 = 0;
    }

    /**
     * G2(r) = Fn*sign(v-r) *[ mu_dyn + mu_stat (if|(v-r)|>vc]
     */
    private double G2( double r ) {
        double x = v - r;
        int signOf = ( x > 0 ? 1 : -1 );
        double absx = Math.abs( x );
        double ret = signOf * fn * ( mu_dynamic + mu_static * ( absx > v_crit ? 0 : 1 ) );
        return ret;
    }

    /**
     * Compute the next buffer.
     * Discrete model with r = dq/dt:
     * r(t+1) = (1-k_d/(srate*M))r(t) - k_s/(srate*M) * q(t)+G(r(t))/(M*srate)
     * q(t+1) = q(t)+r(t)/srate
     */
    public void computeBuffer() {
        double foos = k_spring / ( mass * srate );
        double food = 1. - k_damp / ( mass * srate );
        double bard = fn * mu_dynamic / ( mass * srate );
        double bars = fn * mu_static / ( mass * srate );
        int bufsz = getBufferSize();
        for( int k = 0; k < bufsz; k++ ) {
            double vdiff = v - rt_1;
            double absvdiff, rnew, qnew;
            int signOf;
            if( vdiff > 0 ) {
                absvdiff = vdiff;
                signOf = 1;
            }
            else {
                absvdiff = -vdiff;
                signOf = -1;
            }
            if( absvdiff > v_crit ) { // slip
                rnew = food * rt_1 - foos * qt_1 + bard * signOf;
                qnew = qt_1 + rt_1 / srate;
            }
            else { // sticking
                double breakAwayForce = fn * mu_static;
                double appliedForce = Math.abs( k_spring * qt_1 + k_damp * rt_1 );
                if( appliedForce > breakAwayForce ) { // slipping in static friction region
                    rnew = food * rt_1 - foos * qt_1 + bars * signOf;
                    qnew = qt_1 + rt_1 / srate;
                }
                else { // stick
                    rnew = v;
                    //rnew = rt_1; // zero force
                    qnew = qt_1 + rt_1 / srate;
                }
            }
            buf[k] = (float)( rt_1 + rnew );
            qt_1 = qnew;
            rt_1 = rnew;
            //System.out.println("q="+qnew);
        }
    }

}




