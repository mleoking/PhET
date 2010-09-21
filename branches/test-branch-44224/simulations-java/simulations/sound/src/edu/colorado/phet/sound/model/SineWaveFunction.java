/**
 * Class: SineWaveFunction
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.model;

public class SineWaveFunction implements WaveFunction {

    private Wavefront wavefront;

    public SineWaveFunction( Wavefront wavefront ) {
        this.wavefront = wavefront;
    }

    /**
     * @param time
     * @return
     */
    public double waveAmplitude( double time ) {
        double amplitude = 0.0;
        if( wavefront.getFrequency() != 0 ) {
            amplitude = Math.sin( wavefront.getFrequency() * time ) * wavefront.getMaxAmplitude();
        }
        else {
            amplitude = 0;
        }
        return amplitude;
    }
}
