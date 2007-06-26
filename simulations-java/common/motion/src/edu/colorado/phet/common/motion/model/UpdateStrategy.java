package edu.colorado.phet.common.motion.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 11:36:26 PM
 */
public interface UpdateStrategy {
    void update( MotionBodySeries model, double dt, MotionBodyState state, double time );
}
