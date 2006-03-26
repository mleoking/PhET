/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 4:48:25 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class FaucetOnOffControlPanel extends HorizontalLayoutPanel {
    private Oscillator oscillator;

    public FaucetOnOffControlPanel( final Oscillator oscillator ) {
        this.oscillator = oscillator;
        ButtonGroup buttonGroup = new ButtonGroup();
        final JCheckBox on = new JCheckBox( "On", oscillator.isEnabled() );
        on.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                oscillator.setEnabled( on.isSelected() );
            }
        } );
        buttonGroup.add( on );
        add( on );

        final JCheckBox off = new JCheckBox( "Off", !oscillator.isEnabled() );
        off.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                oscillator.setEnabled( !off.isSelected() );
            }
        } );
        buttonGroup.add( off );
        add( off );

        setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.blue, 2 ), "Faucet" ) );
    }
}
