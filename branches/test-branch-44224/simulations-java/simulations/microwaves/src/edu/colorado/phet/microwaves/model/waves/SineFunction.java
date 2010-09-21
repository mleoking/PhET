/**
 * Class: SineFunction
 * Package: edu.colorado.phet.waves.model
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.microwaves.model.waves;

public class SineFunction implements PeriodicFunction {

    public SineFunction() {
    }

    /**
     * @param time
     * @return
     */
    public float valueAtTime( float frequency, float maxAmplitude, float time ) {
        float amplitude = 0.0f;
        if ( frequency != 0 ) {
            amplitude = (float) Math.sin( frequency * time * Math.PI * 2 ) * maxAmplitude;
        }
        else {
            amplitude = 0;
        }
        return amplitude;
    }
}
