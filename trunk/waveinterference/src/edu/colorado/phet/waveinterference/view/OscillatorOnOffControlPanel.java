/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 2:11:57 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class OscillatorOnOffControlPanel extends VerticalLayoutPanel {
    private Oscillator oscillator;
    private JRadioButton onRadioButton;
    private JRadioButton offRadioButton;

    public OscillatorOnOffControlPanel( final Oscillator oscillator ) {
        this.oscillator = oscillator;
        ButtonGroup buttonGroup = new ButtonGroup();
        onRadioButton = new JRadioButton( "On", oscillator.isEnabled() );
        onRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                oscillator.setEnabled( onRadioButton.isSelected() );
            }
        } );
        buttonGroup.add( onRadioButton );
        add( onRadioButton );

        offRadioButton = new JRadioButton( "Off", !oscillator.isEnabled() );
        offRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                oscillator.setEnabled( !offRadioButton.isSelected() );
            }
        } );

        oscillator.addListener( new Oscillator.Listener() {
            public void enabledStateChanged() {
                updateState();
            }

            public void locationChanged() {
            }

            public void frequencyChanged() {
            }
        } );

        buttonGroup.add( offRadioButton );
        add( offRadioButton );
    }

    private void updateState() {
        onRadioButton.setSelected( oscillator.isEnabled() );
        offRadioButton.setSelected( !oscillator.isEnabled() );
    }
}
