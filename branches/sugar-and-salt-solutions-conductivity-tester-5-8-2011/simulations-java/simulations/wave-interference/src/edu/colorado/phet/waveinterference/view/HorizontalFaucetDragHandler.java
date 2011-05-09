// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 18, 2006
 * Time: 12:58:21 PM
 */

public class HorizontalFaucetDragHandler extends FaucetDragHandler {

    public HorizontalFaucetDragHandler( FaucetGraphic faucetGraphic ) {
        super( faucetGraphic );
    }

    protected void applyOffset( Point offset ) {
        super.getOscillator().setLocation( super.getOriginalOscillatorLocation().x + offset.x, getOriginalOscillatorLocation().y );
    }
}
