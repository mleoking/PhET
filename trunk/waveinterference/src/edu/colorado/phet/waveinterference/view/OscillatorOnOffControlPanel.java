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

    public OscillatorOnOffControlPanel( final Oscillator oscillator ) {
        this.oscillator = oscillator;
        ButtonGroup buttonGroup = new ButtonGroup();
        final JRadioButton on = new JRadioButton( "On", oscillator.isEnabled() );
        on.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                oscillator.setEnabled( on.isSelected() );
            }
        } );
        buttonGroup.add( on );
        add( on );

        final JRadioButton off = new JRadioButton( "Off", !oscillator.isEnabled() );
        off.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                oscillator.setEnabled( !off.isSelected() );
            }
        } );
        buttonGroup.add( off );
        add( off );
    }
}
