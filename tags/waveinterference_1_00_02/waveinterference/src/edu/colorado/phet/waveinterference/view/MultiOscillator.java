/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:41:32 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MultiOscillator {
    boolean twoSources;
    private double spacing = DEFAULT_SPACING;
    private WaveModel waveModel;
    private PNode primary;
    private Oscillator primaryOscillator;
    private PNode secondary;
    private Oscillator secondaryOscillator;
    private int oscillatorX = 5;

    private static final double DEFAULT_SPACING = 10;

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
        notifyListeners();
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
        setOneDrip();
        setSpacing( DEFAULT_SPACING );
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void multiOscillatorChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.multiOscillatorChanged();
        }
    }
}
