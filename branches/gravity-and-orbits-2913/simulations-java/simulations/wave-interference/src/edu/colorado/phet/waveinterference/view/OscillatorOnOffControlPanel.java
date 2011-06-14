// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 2:11:57 PM
 */

public class OscillatorOnOffControlPanel extends VerticalLayoutPanel {
    private Oscillator oscillator;
    private JRadioButton onRadioButton;
    private JRadioButton offRadioButton;
    private PulseButton pulseButton;

    public OscillatorOnOffControlPanel( final Oscillator oscillator ) {
        this.oscillator = oscillator;
        ButtonGroup buttonGroup = new ButtonGroup();
        onRadioButton = new JRadioButton( WIStrings.getString( "controls.on" ), oscillator.isEnabled() );
        onRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                oscillator.setEnabled( onRadioButton.isSelected() );
                updatePulseEnabled();
            }
        } );
        buttonGroup.add( onRadioButton );
        add( onRadioButton );

        offRadioButton = new JRadioButton( WIStrings.getString( "controls.off" ), !oscillator.isEnabled() );
        offRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                oscillator.setEnabled( !offRadioButton.isSelected() );
                updatePulseEnabled();
            }
        } );

        oscillator.addListener( new Oscillator.Adapter() {
            public void enabledStateChanged() {
                updateState();
            }
        } );

        buttonGroup.add( offRadioButton );
        add( offRadioButton );

        pulseButton = new PulseButton( oscillator );
        add( pulseButton );
        updatePulseEnabled();
    }

    private void updatePulseEnabled() {
//        pulse.setEnabled( offRadioButton.isSelected() );
    }

    private void updateState() {
        onRadioButton.setSelected( oscillator.isEnabled() );
        offRadioButton.setSelected( !oscillator.isEnabled() );
    }
}
