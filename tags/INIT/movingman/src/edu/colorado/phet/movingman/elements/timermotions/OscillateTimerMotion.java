package edu.colorado.phet.movingman.elements.timermotions;

import edu.colorado.phet.movingman.elements.TimerMotion;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 3, 2003
 * Time: 11:11:51 PM
 * To change this template use Options | File Templates.
 */
public class OscillateTimerMotion implements TimerMotion {
    double frequency;
    double amplitude;
    private double origin;
    private double phase;

    public OscillateTimerMotion(double frequency, double amplitude) {
        this(frequency, amplitude, 0, 0);
    }

    public OscillateTimerMotion(double frequency, double amplitude, double origin, double phase) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.origin = origin;
        this.phase = phase;
    }

    public double getPosition(double time) {
        return Math.sin(frequency * time + phase) * amplitude + origin;
    }

    public void setInitialPosition(double x) {
        this.origin = x;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public void setAmplitude(double amp) {
        this.amplitude = amp;
    }
}
