/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

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
    private boolean enabled = true;
    private ArrayList listeners = new ArrayList();
    private Oscillator prototype;

    private Oscillator( WaveModel waveModel, boolean prototype ) {
        this( waveModel );
    }

    public Oscillator( WaveModel waveModel ) {
        this( waveModel, 8, waveModel.getHeight() / 2 );
    }

    public Oscillator( WaveModel waveModel, int x, int y ) {
        this.waveModel = waveModel;
        setLocation( x, y );
    }

    public void setTime( double t ) {
        this.time = t;
        if( isEnabled() ) {
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
    }

    public double getValue() {
        return evaluate( time );
    }

    public double getVelocity() {
        return evaluateVelocity( time );
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
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.locationChanged();
        }
    }

    public double getFrequency() {
        return 1 / period;
    }

    public void setFrequency( double value ) {
        this.period = 1.0 / value;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.frequencyChanged();
        }
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude( double amplitude ) {
        this.amplitude = amplitude;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.amplitudeChanged();
        }
    }

    public int getCenterX() {
        return x;
    }

    public int getCenterY() {
        return y;
    }

    public double getTime() {
        return time;
    }

    public double evaluate( double tEval ) {
        return amplitude * Math.cos( 2 * Math.PI * getFrequency() * tEval );
    }

    public double evaluateVelocity( double tEval ) {
        return amplitude * Math.sin( 2 * Math.PI * getFrequency() * tEval ) * 2 * Math.PI * getFrequency();
    }

    public void setEnabled( boolean selected ) {
        if( !this.enabled == selected ) {
            this.enabled = selected;
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.enabledStateChanged();
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Point2D getCenter() {
        return new Point2D.Double( getCenterX(), getCenterY() );
    }

    public void reset() {
        setAmplitude( prototype.getAmplitude() );
        setEnabled( prototype.isEnabled() );
        setFrequency( prototype.getFrequency() );
        setLocation( prototype.getCenterX(), prototype.getCenterY() );
        setPeriod( prototype.getPeriod() );
        setRadius( prototype.getRadius() );
        setTime( prototype.getTime() );
    }

    public void saveState() {//for initial conditions.
        this.prototype = new Oscillator( waveModel, true );
        this.prototype.setAmplitude( getAmplitude() );
        this.prototype.setEnabled( isEnabled() );
        this.prototype.setFrequency( getFrequency() );
        this.prototype.setLocation( getCenterX(), getCenterY() );
        this.prototype.setPeriod( getPeriod() );
        this.prototype.setTime( time );
    }

    public static interface Listener {
        void enabledStateChanged();

        void locationChanged();

        void frequencyChanged();

        void amplitudeChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static class Adapter implements Listener {
        public void enabledStateChanged() {
        }

        public void locationChanged() {
        }

        public void frequencyChanged() {
        }

        public void amplitudeChanged() {
        }
    }
}
