/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 18, 2006
 * Time: 12:58:21 PM
 * Copyright (c) May 18, 2006 by Sam Reid
 */

public class HorizontalFaucetDragHandler extends FaucetDragHandler {

    public HorizontalFaucetDragHandler( FaucetGraphic faucetGraphic ) {
        super( faucetGraphic );
    }

    protected void applyOffset( Point offset ) {
        super.getOscillator().setLocation( super.getOriginalOscillatorLocation().x + offset.x, getOriginalOscillatorLocation().y );
    }
}
