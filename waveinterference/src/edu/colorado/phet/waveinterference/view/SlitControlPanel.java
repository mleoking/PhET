/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.SlitPotential;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:28:16 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class SlitControlPanel extends VerticalLayoutPanel {
    private SlitPotential slitPotential;
    private ModelSlider slitSeparation;

    public SlitControlPanel( final SlitPotential slitPotential ) {
        setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Barrier" ) );
        this.slitPotential = slitPotential;
        HorizontalLayoutPanel topPanel = new HorizontalLayoutPanel();
        final JCheckBox enable = new JCheckBox( "Enabled", slitPotential.isEnabled() );
        enable.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setEnabled( enable.isSelected() );
            }
        } );
        topPanel.add( enable );

        ButtonGroup buttonGroup = new ButtonGroup();
        final JRadioButton oneSlit = new JRadioButton( "One Slit", slitPotential.isOneSlit() );
        final JRadioButton twoSlits = new JRadioButton( "Two Slits", slitPotential.isTwoSlits() );
        buttonGroup.add( oneSlit );
        buttonGroup.add( twoSlits );
        oneSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                slitPotential.setOneSlit();
                updateSeparationCheckbox();
            }
        } );
        twoSlits.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                slitPotential.setTwoSlits();
                updateSeparationCheckbox();
            }
        } );
        VerticalLayoutPanel eastPanel = new VerticalLayoutPanel();
        eastPanel.add( oneSlit );
        eastPanel.add( twoSlits );

        topPanel.add( eastPanel );
        add( topPanel );

        final ModelSlider slitWidth = new ModelSlider( "Slit Width", "", 0, 30, slitPotential.getSlitWidth() );
        slitWidth.setTextFieldVisible( false );
        slitWidth.setBorder( null );
        slitWidth.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setSlitWidth( (int)slitWidth.getValue() );
            }
        } );
        add( slitWidth );

        final ModelSlider slitLocation = new ModelSlider( "Barrier Location", "", 0, 100, slitPotential.getLocation() );
        slitLocation.setTextFieldVisible( false );
        slitLocation.setBorder( null );
        slitLocation.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setLocation( (int)slitLocation.getValue() );
            }
        } );
        add( slitLocation );

        slitSeparation = new ModelSlider( "Slit Separation", "", 0, 50, slitPotential.getSlitSeparation() );
        slitSeparation.setTextFieldVisible( false );
        slitSeparation.setBorder( null );
        slitSeparation.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setSlitSeparation( (int)slitSeparation.getValue() );
            }
        } );
        add( slitSeparation );
        updateSeparationCheckbox();
    }

    private void updateSeparationCheckbox() {
        slitSeparation.setEnabled( !slitPotential.isOneSlit() );
    }
}
