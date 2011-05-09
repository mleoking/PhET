// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 4:48:25 PM
 */

public class FaucetOnOffControlPanel extends HorizontalLayoutPanel {
    private FaucetGraphic faucetGraphic;
    private JRadioButton onRadioButton;
    private JRadioButton offRadioButton;
    private PulseButton pulseButton;

    public FaucetOnOffControlPanel( final FaucetGraphic faucetGraphic ) {
        this.faucetGraphic = faucetGraphic;
        ButtonGroup buttonGroup = new ButtonGroup();
        onRadioButton = new JRadioButton( WIStrings.getString( "controls.on" ), faucetGraphic.isEnabled() );
        onRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                faucetGraphic.setEnabled( onRadioButton.isSelected() );
            }
        } );
        buttonGroup.add( onRadioButton );
        add( onRadioButton );

        offRadioButton = new JRadioButton( WIStrings.getString( "controls.off" ), !faucetGraphic.isEnabled() );
        offRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                faucetGraphic.setEnabled( !offRadioButton.isSelected() );
            }
        } );
        buttonGroup.add( offRadioButton );
        add( offRadioButton );

        faucetGraphic.addListener( new FaucetGraphic.Listener() {
            public void enabledStateChanged() {
                updateState();
            }
        } );
        faucetGraphic.getOscillator().addListener( new Oscillator.Adapter() {
            public void enabledStateChanged() {
                onRadioButton.setSelected( faucetGraphic.getOscillator().isEnabled() );
                offRadioButton.setSelected( !faucetGraphic.getOscillator().isEnabled() );
            }
        } );
        updateState();
        pulseButton = new PulseButton( faucetGraphic.getOscillator() );
        add( pulseButton );
    }

    private void updateState() {
        onRadioButton.setSelected( faucetGraphic.isEnabled() );
        offRadioButton.setSelected( !faucetGraphic.isEnabled() );
    }
}
