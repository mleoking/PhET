package jass.contact;

import jass.engine.Out;

/**
 A force model based on stick slip model.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class StickSlip extends Out {

    private float srate; // sampling rate in Hertz
    private float k_damp; // damping of model/srate
    private float mu_static; // static friction coeff.
    private float mu_dynamic; // dynamic friction coeff.
    private float v_crit; // critical velocity where static friction kicks in/srate
    private float qt_1,qt_2; // last state
    private float v = 1; // external slidevelocity in m/s / srate
    private float fn = 1; // external; normal force/srate

    /** Construct force.
     @param srate sampling rate in Hertz.
     @param bufferSize bufferSize of this Out.
     */
    public StickSlip(float srate, int bufferSize) {
        super(bufferSize);
        this.srate = srate;
        reset();
    }

    /** Set stick-slip model parameters (all in S.I. units).
     @param k_damp damping of spring system
     @param dummy not used
     @param mu_static static friction coeff
     @param mu_dynamic dynamic friction coeff
     @param v_crit critical velocity for transition from dyn. to static friction
     */
    public void setStickSlipParameters(float k_damp, float dummy, float mu_static, float mu_dynamic, float v_crit) {
        this.k_damp = k_damp / srate;
        this.mu_static = mu_static;
        this.mu_dynamic = mu_dynamic;
        this.v_crit = v_crit / srate;
    }

    /** Set normal force magnitude.
     @param val normal force in Newton
     */
    public void setNormalForce(float val) {
        fn = val / srate;
    }

    /** Set velocity.
     @param v velocity in m/s
     */
    public void setSpeed(float v) {
        this.v = v / srate;
    }

    /** Reset state.
     */
    public void reset() {
        qt_1 = qt_2 = 0;
    }

    /** Compute the next buffer.
     q(t+1) = (1-k_damp)q(t)+fn mu[(v - (q(t)-q(t-1)))] * sign(v - (q(t)-q(t-1))
     */
    public void computeBuffer() {
        float factor = (1 - k_damp);
        int bufsz = getBufferSize();
        for (int k = 0; k < bufsz; k++) {
            float f_mu_sign;
            float tmp = v - qt_1 + qt_2;
            int signOf = (tmp >= 0 ? 1 : -1);
            if ((float) (Math.abs(tmp)) > v_crit) {
                f_mu_sign = fn * mu_dynamic * signOf;
            } else {
                f_mu_sign = fn * mu_static * signOf;
            }
            buf[k] = factor * qt_1 + f_mu_sign;
            qt_2 = qt_1;
            qt_1 = buf[k];
        }
    }

}


