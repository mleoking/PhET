/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 9:59:46 AM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class FaucetControlPanel extends HorizontalLayoutPanel {
    private Oscillator oscillator;
    private FaucetGraphic faucetGraphic;

    public FaucetControlPanel( Oscillator oscillator, FaucetGraphic faucetGraphic ) {
        this.oscillator = oscillator;
        this.faucetGraphic = faucetGraphic;
        addControl( new OscillatorControlPanel( oscillator ) );
//        addControl( new FaucetOnOffControlPanel( faucetGraphic ) );
        setBorder( BorderFactory.createRaisedBevelBorder() );
    }

    private void addControl( JComponent component ) {
        add( component );
    }

    public Oscillator getOscillator() {
        return oscillator;
    }

    public FaucetGraphic getFaucetGraphic() {
        return faucetGraphic;
    }
}
