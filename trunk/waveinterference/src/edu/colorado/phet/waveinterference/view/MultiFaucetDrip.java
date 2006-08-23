/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:41:32 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MultiFaucetDrip {//todo should this extend pnode, with primary & secondary as children?
    private boolean twoDrips;
    private double spacing = 10;
    private WaveModel waveModel;
    private FaucetGraphic primary;
    private FaucetGraphic secondary;
    private int oscillatorX = 5;

    private ArrayList listeners = new ArrayList();

    public MultiFaucetDrip( WaveModel waveModel, FaucetGraphic primary, FaucetGraphic secondary ) {
        this.waveModel = waveModel;
        this.primary = primary;
        this.secondary = secondary;
        primary.getOscillator().addListener( new Oscillator.Adapter() {

            public void frequencyChanged() {
                updateSecondary();
            }

            public void amplitudeChanged() {
                updateSecondary();
            }

        } );
        setOneDrip();
        updateSecondary();
    }

    public void reset() {
        setOneDrip();
    }

    public static interface Listener {
        void spacingChanged();

        void dropCountChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifySpacingChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.spacingChanged();
        }
    }

    private void updateSecondary() {
        secondary.getOscillator().setAmplitude( primary.getOscillator().getAmplitude() );
        secondary.getOscillator().setFrequency( primary.getOscillator().getFrequency() );
    }

    public boolean isOneDrip() {
        return !isTwoDrip();
    }

    public boolean isTwoDrip() {
        return twoDrips;
    }

    public void setOneDrip() {
        this.twoDrips = false;
        update();
        notifyDropCountChanged();
    }

    private void notifyDropCountChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.dropCountChanged();
        }
    }

    private void update() {
        secondary.setEnabled( twoDrips );
        secondary.setVisible( twoDrips );
        if( twoDrips ) {
            primary.getOscillator().setLocation( oscillatorX, (int)( waveModel.getHeight() / 2 - spacing ) );
            secondary.getOscillator().setLocation( oscillatorX, (int)( waveModel.getHeight() / 2 + spacing ) );
        }
        else {
            primary.getOscillator().setLocation( oscillatorX, waveModel.getHeight() / 2 );
        }
        if( twoDrips ) {
            primary.setVerticalDrag();
            secondary.setVerticalDrag();
        }
        else {
            primary.setHorizontalDrag();
        }
    }

    public void setTwoDrips() {
        this.twoDrips = true;
        update();
        notifyDropCountChanged();
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing( double value ) {
        this.spacing = value;
        update();

        notifySpacingChanged();
    }

    public FaucetGraphic getPrimary() {
        return primary;
    }

    public FaucetGraphic getSecondary() {
        return secondary;
    }
}
