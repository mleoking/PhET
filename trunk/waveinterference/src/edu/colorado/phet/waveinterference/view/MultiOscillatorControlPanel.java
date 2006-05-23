/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.ModelSlider;

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

public class MultiOscillatorControlPanel extends VerticalLayoutPanelWithDisable {
    private MultiOscillator multiOscillator;
    private WaveInterferenceScreenUnits units;
    private ModelSlider spacingSlider;
    private JRadioButton oneDrip;
    private JRadioButton twoDrips;

    public MultiOscillatorControlPanel( final MultiOscillator multiOscillator, String name, WaveInterferenceScreenUnits units ) {
        this.multiOscillator = multiOscillator;
        this.units = units;
        setBorder( BorderFactory.createEtchedBorder() );
        oneDrip = new JRadioButton( "One " + name, multiOscillator.isOneSource() );
        oneDrip.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                multiOscillator.setOneDrip();
                updateSpacingSlider();
            }
        } );
        twoDrips = new JRadioButton( "Two " + name + "s", multiOscillator.isTwoSource() );
        twoDrips.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                multiOscillator.setTwoDrips();
                updateSpacingSlider();
            }
        } );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( oneDrip );
        buttonGroup.add( twoDrips );
        add( oneDrip );
        add( twoDrips );
        spacingSlider = new ModelSlider( "Spacing", "m", 0, 30, multiOscillator.getSpacing() );
        spacingSlider.setModelLabels( units.toHashtable( new int[]{0, 15, 30}, 2 ) );
        spacingSlider.setTextFieldVisible( false );
        spacingSlider.setBorder( null );
        spacingSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                multiOscillator.setSpacing( spacingSlider.getValue() );
            }
        } );
        add( spacingSlider );
        updateSpacingSlider();
    }

    private void updateSpacingSlider() {
        spacingSlider.setEnabled( multiOscillator.isTwoSource() && getEnabledFlag() );
    }

    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        updateSpacingSlider();
    }
}
