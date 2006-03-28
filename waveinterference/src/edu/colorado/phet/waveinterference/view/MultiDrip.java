/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.WaveModel;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:41:32 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MultiDrip {
    boolean twoDrips;
    private double spacing = 10;
    private WaveModel waveModel;
    private FaucetGraphic primary;
    private FaucetGraphic secondary;
    private int oscillatorX = 5;

    public MultiDrip( WaveModel waveModel, FaucetGraphic primary, FaucetGraphic secondary ) {
        this.waveModel = waveModel;
        this.primary = primary;
        this.secondary = secondary;
        setOneDrip();
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
    }

    public void setTwoDrips() {
        this.twoDrips = true;
        update();
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing( double value ) {
        this.spacing = value;
        update();
    }
}
