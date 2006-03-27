/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:41:32 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MultiDrip {
    boolean twoDrips;
    private double spacing = 10;
    private FaucetGraphic primary;
    private FaucetGraphic secondary;

    public MultiDrip( FaucetGraphic primary, FaucetGraphic secondary ) {
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
        secondary.setEnabled( false );
        secondary.setVisible( false );
    }

    public void setTwoDrips() {
        this.twoDrips = true;
        secondary.setEnabled( true );
        secondary.setVisible( true );
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing( double value ) {
        this.spacing = value;
    }
}
