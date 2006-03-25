/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 1:28:36 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class Oscillator {
    private double period = 2;
    private int oscillatorRadius = 2;
    private WaveModel waveModel;
    private int x;
    private int y;
    private double amplitude = 1.0;
    private double time;

    public Oscillator( WaveModel waveModel ) {
        this( waveModel, 8, waveModel.getHeight() / 2 );
    }

    public Oscillator( WaveModel waveModel, int x, int y ) {
        this.waveModel = waveModel;
        this.x = x;
        this.y = y;
    }

    public void setTime( double t ) {
        this.time = t;
        double value = getValue();
        for( int i = x - oscillatorRadius; i <= x + oscillatorRadius; i++ ) {
            for( int j = y - oscillatorRadius; j <= y + oscillatorRadius; j++ ) {
                if( Math.sqrt( ( i - x ) * ( i - x ) + ( j - y ) * ( j - y ) ) < oscillatorRadius ) {
                    if( waveModel.containsLocation( i, j ) ) {
                        waveModel.setSourceValue( i, j, (float)value );
                    }
                }
            }
        }
    }

    public double getValue() {
        return amplitude * Math.cos( 2 * Math.PI * getFrequency() * time );
    }

    public double getVelocity() {
        return amplitude * Math.sin( 2 * Math.PI * getFrequency() * time ) * 2 * Math.PI * getFrequency();
    }

    public int getRadius() {
        return oscillatorRadius;
    }

    public void setRadius( int oscillatorRadius ) {
        this.oscillatorRadius = oscillatorRadius;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod( double value ) {
        this.period = value;
    }

    public void setLocation( int x, int y ) {
        this.x = x;
        this.y = y;
    }

    public double getFrequency() {
        return 1 / period;
    }

    public void setFrequency( double value ) {
        this.period = 1.0 / value;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude( double amplitude ) {
        this.amplitude = amplitude;
    }

    public int getCenterX() {
        return x;
    }

    public int getCenterY() {
        return y;
    }
}
