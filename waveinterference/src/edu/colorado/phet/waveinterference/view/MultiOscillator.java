/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:41:32 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MultiOscillator {
    boolean twoSources;
    private double spacing = 10;
    private WaveModel waveModel;
    private PNode primary;
    private Oscillator primaryOscillator;
    private PNode secondary;
    private Oscillator secondaryOscillator;
    private int oscillatorX = 5;

    public MultiOscillator( WaveModel waveModel, PNode primary, final Oscillator primaryOscillator, PNode secondary, final Oscillator secondaryOscillator ) {
        this.waveModel = waveModel;
        this.primary = primary;
        this.primaryOscillator = primaryOscillator;
        this.secondary = secondary;
        this.secondaryOscillator = secondaryOscillator;
        primaryOscillator.addListener( new Oscillator.Adapter() {
            public void frequencyChanged() {
                secondaryOscillator.setFrequency( primaryOscillator.getFrequency() );
            }

            public void amplitudeChanged() {
                secondaryOscillator.setAmplitude( primaryOscillator.getAmplitude() );
            }
        } );
        secondaryOscillator.setFrequency( primaryOscillator.getFrequency() );
        secondaryOscillator.setAmplitude( primaryOscillator.getAmplitude() );
        setOneDrip();
    }

    public boolean isOneSource() {
        return !isTwoSource();
    }

    public boolean isTwoSource() {
        return twoSources;
    }

    public void setOneDrip() {
        this.twoSources = false;
        update();
    }

    private void update() {
        secondaryOscillator.setEnabled( twoSources );
        secondary.setVisible( twoSources );
        if( twoSources ) {
            primaryOscillator.setLocation( oscillatorX, (int)( waveModel.getHeight() / 2 - spacing ) );
            secondaryOscillator.setLocation( oscillatorX, (int)( waveModel.getHeight() / 2 + spacing ) );
        }
        else {
            primaryOscillator.setLocation( oscillatorX, waveModel.getHeight() / 2 );
        }
    }

    public void setTwoDrips() {
        this.twoSources = true;
        update();
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing( double value ) {
        this.spacing = value;
        update();
    }

    public void reset() {
    }
}
