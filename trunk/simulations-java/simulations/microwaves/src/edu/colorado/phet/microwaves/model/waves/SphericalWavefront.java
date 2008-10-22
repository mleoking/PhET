/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 22, 2003
 * Time: 10:01:06 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.microwaves.model.waves;

public class SphericalWavefront implements WavefrontType {

    public float computeAmplitudeAtDistance( Wave wavefront,
                                             float amplitude,
                                             float distance ) {
        float[] amplitudes = wavefront.getAmplitude();
        float factor = 1.0f - (float) ( 0.05 * distance / amplitudes.length );
//        float factor = 1.0 - ( (float)wavefront.getPropagationSpeed()) / ( amplitudes.length - (int)distance );

        if ( factor < 0 ) {
            System.out.println( "***" );
        }
        return amplitude * factor;
    }
}
