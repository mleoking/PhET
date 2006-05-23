/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:38:54 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MultiDripControlPanel extends VerticalLayoutPanel {
    private ModelSlider spacingSlider;
    private JRadioButton twoDrips;
    private JRadioButton oneDrip;
    private MultiFaucetDrip multiFaucetDrip;
    private WaveInterferenceScreenUnits screenUnits;

    public MultiDripControlPanel( final MultiFaucetDrip multiFaucetDrip, WaveInterferenceScreenUnits screenUnits ) {
        this.multiFaucetDrip = multiFaucetDrip;
        this.screenUnits = screenUnits;
        setBorder( BorderFactory.createEtchedBorder() );
        oneDrip = new JRadioButton( "One Drip", multiFaucetDrip.isOneDrip() );
        oneDrip.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                multiFaucetDrip.setOneDrip();
                updateSpacingSlider();
            }
        } );
        twoDrips = new JRadioButton( "Two Drips", multiFaucetDrip.isTwoDrip() );
        twoDrips.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                multiFaucetDrip.setTwoDrips();
                updateSpacingSlider();
            }
        } );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( oneDrip );
        buttonGroup.add( twoDrips );
        add( oneDrip );
        add( twoDrips );
        spacingSlider = new ModelSlider( "Spacing", "m", 0, 30, multiFaucetDrip.getSpacing() );
        spacingSlider.setModelLabels( screenUnits.toHashtable( new int[]{0, 15, 30}, 2 ) );
        spacingSlider.setTextFieldVisible( false );
        spacingSlider.setBorder( null );
        spacingSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                multiFaucetDrip.setSpacing( spacingSlider.getValue() );
            }
        } );
        add( spacingSlider );
        updateSpacingSlider();
    }

    private void updateSpacingSlider() {
        spacingSlider.setEnabled( multiFaucetDrip.isTwoDrip() );
    }
}
