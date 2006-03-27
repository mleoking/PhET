/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 9:59:46 AM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class FaucetControlPanel extends VerticalLayoutPanel {
    public FaucetControlPanel( Oscillator oscillator, FaucetGraphic faucetGraphic ) {
        setFillHorizontal();
        addControl( new OscillatorControlPanel( oscillator ) );
//        addControl( new FaucetOnOffControlPanel( faucetGraphic ) );
    }

    private void addControl( JComponent component ) {
        add( component );
    }
}
