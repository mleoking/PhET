/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 4:48:25 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class FaucetOnOffControlPanel extends HorizontalLayoutPanel {
    private FaucetGraphic faucetGraphic;
    private JRadioButton onRadioButton;
    private JRadioButton offRadioButton;

    public FaucetOnOffControlPanel( final FaucetGraphic faucetGraphic ) {
        this.faucetGraphic = faucetGraphic;
        ButtonGroup buttonGroup = new ButtonGroup();
        onRadioButton = new JRadioButton( WIStrings.getString( "on" ), faucetGraphic.isEnabled() );
        onRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                faucetGraphic.setEnabled( onRadioButton.isSelected() );
            }
        } );
        buttonGroup.add( onRadioButton );
        add( onRadioButton );

        offRadioButton = new JRadioButton( WIStrings.getString( "off" ), !faucetGraphic.isEnabled() );
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
//        setBorder( BorderFactory.createTitledBorder( BorderFactory.createLineBorder( Color.blue, 2 ), "Faucet" ) );
    }

    private void updateState() {
        onRadioButton.setSelected( faucetGraphic.isEnabled() );
        offRadioButton.setSelected( !faucetGraphic.isEnabled() );
    }
}
