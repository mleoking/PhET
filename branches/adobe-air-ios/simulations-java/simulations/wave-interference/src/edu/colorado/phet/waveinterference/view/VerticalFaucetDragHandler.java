// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 18, 2006
 * Time: 12:58:21 PM
 */

public class VerticalFaucetDragHandler extends FaucetDragHandler {

    public VerticalFaucetDragHandler( FaucetGraphic faucetGraphic ) {
        super( faucetGraphic );
    }

    protected void applyOffset( Point offset ) {
        super.getOscillator().setLocation( super.getOriginalOscillatorLocation().x, getOriginalOscillatorLocation().y + offset.y );
    }
}
