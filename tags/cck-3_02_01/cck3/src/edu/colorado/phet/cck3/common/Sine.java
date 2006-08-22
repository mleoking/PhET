/**
 * Class: Sine
 * Package: edu.colorado.phet.waves.model
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.cck3.common;

public class Sine {
    private double frequency;
    private double amplitude;
    private double phase = 0;

    public Sine( double amplitude, double frequency ) {
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    public double valueAtTime( double time ) {
        double value = 0.0;
        if( frequency != 0 ) {
            value = Math.sin( frequency * time * Math.PI * 2 - phase ) * amplitude;
        }
        else {
            value = 0;
        }
        return value;
    }
}
