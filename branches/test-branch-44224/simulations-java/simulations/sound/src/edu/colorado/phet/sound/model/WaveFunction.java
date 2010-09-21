/**
 * Class: WaveFunction
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound.model;

public interface WaveFunction {

    /**
     * Gives the amplitude of the wave funtion at a specified time
     *
     * @param time
     * @return
     */
    double waveAmplitude( double time );
}
