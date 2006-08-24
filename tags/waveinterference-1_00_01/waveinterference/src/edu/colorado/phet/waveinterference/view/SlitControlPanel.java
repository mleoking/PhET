/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:28:16 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class SlitControlPanel extends VerticalLayoutPanelWithDisable {
    private SlitPotential slitPotential;
    private ModelSlider slitSeparation;
    private ModelSlider slitWidthSlider;
    private ModelSlider slitLocationSlider;
    private WaveInterferenceScreenUnits units;
    private JCheckBox enableCheckBox;

    public SlitControlPanel( final SlitPotential slitPotential, WaveInterferenceScreenUnits waveInterferenceScreenUnits ) {
        this.units = waveInterferenceScreenUnits;
        setBorder( BorderFactory.createEtchedBorder() );
        this.slitPotential = slitPotential;
        HorizontalLayoutPanel topPanel = new HorizontalLayoutPanel();
        enableCheckBox = new JCheckBox( WIStrings.getString( "enabled" ), slitPotential.isEnabled() );
        enableCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setEnabled( enableCheckBox.isSelected() );
            }
        } );

        ButtonGroup buttonGroup = new ButtonGroup();
        final JRadioButton noBarrier = new JRadioButton( WIStrings.getString( "no.barrier" ), !slitPotential.isEnabled() );
        final JRadioButton oneSlit = new JRadioButton( WIStrings.getString( "one.slit" ), slitPotential.isOneSlit() && slitPotential.isEnabled() );
        final JRadioButton twoSlits = new JRadioButton( WIStrings.getString( "two.slits" ), slitPotential.isTwoSlits() && slitPotential.isEnabled() );
        buttonGroup.add( noBarrier );
        buttonGroup.add( oneSlit );
        buttonGroup.add( twoSlits );
        noBarrier.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateUIToReflectSlitEnabled( !noBarrier.isSelected() );
            }

        } );
        oneSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateUIToReflectSlitEnabled( true );
                slitPotential.setOneSlit();
                updateSeparationCheckbox();
            }
        } );
        twoSlits.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateUIToReflectSlitEnabled( true );
                slitPotential.setTwoSlits();
                updateSeparationCheckbox();
            }
        } );
        VerticalLayoutPanel eastPanel = new VerticalLayoutPanel();
        eastPanel.add( noBarrier );
        eastPanel.add( oneSlit );
        eastPanel.add( twoSlits );

        topPanel.add( eastPanel );
        add( topPanel );

        slitWidthSlider = new ModelSlider( WIStrings.getString( "slit.width" ), "", 0, 30, slitPotential.getSlitWidth() );//measured in cells
//        slitWidthSlider.setPaintLabels( false );
        slitWidthSlider.setModelLabels( toHashtable( new int[]{0, 15, 30} ) );
        slitWidthSlider.setTextFieldVisible( false );
        slitWidthSlider.setBorder( null );
        slitWidthSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setSlitWidth( (int)slitWidthSlider.getValue() );
            }
        } );
        add( slitWidthSlider );

        slitLocationSlider = new ModelSlider( WIStrings.getString( "barrier.location" ), "", 0, 75, slitPotential.getLocation() );
        slitLocationSlider.setModelLabels( toHashtable( new int[]{0, 75 / 2, 75} ) );
        slitLocationSlider.setTextFieldVisible( false );
        slitLocationSlider.setBorder( null );
        slitLocationSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setLocation( (int)slitLocationSlider.getValue() );
            }
        } );
        add( slitLocationSlider );

        slitSeparation = new ModelSlider( WIStrings.getString( "slit.separation" ), "", 0, 50, slitPotential.getSlitSeparation() );
        slitSeparation.setModelLabels( toHashtable( new int[]{0, 25, 50} ) );
        slitSeparation.setTextFieldVisible( false );
        slitSeparation.setBorder( null );
        slitSeparation.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                slitPotential.setSlitSeparation( (int)slitSeparation.getValue() );
            }
        } );
        add( slitSeparation );
        updateSeparationCheckbox();
        updateUIToReflectSlitEnabled( slitPotential.isEnabled() );
        slitPotential.addListener( new SlitPotential.Listener() {
            public void slitsChanged() {
                slitSeparation.setValue( slitPotential.getSlitSeparation() );
                slitWidthSlider.setValue( slitPotential.getSlitWidth() );
                slitLocationSlider.setValue( slitPotential.getLocation() );
                enableCheckBox.setSelected( slitPotential.isEnabled() );
                if( !slitPotential.isEnabled() ) {
                    noBarrier.setSelected( true );
                }
                else if( slitPotential.isOneSlit() ) {
                    oneSlit.setSelected( true );
                }
                else {
                    twoSlits.setSelected( true );
                }
                updateUIToReflectSlitEnabled( slitPotential.isEnabled() );
            }
        } );
    }

    private Hashtable toHashtable( int[] doubles ) {
        return units.toHashtable( doubles );
    }

    private void updateSeparationCheckbox() {
        boolean a = !slitPotential.isOneSlit();
        boolean b = slitPotential.isEnabled();
        boolean c = getEnabledFlag();
//        System.out.println( "a = " + a+", b="+b+", c="+c );//debugging
        slitSeparation.setEnabled( a && b && c );
    }

    private void updateUIToReflectSlitEnabled( boolean b ) {
        slitPotential.setEnabled( b );
        slitWidthSlider.setEnabled( b );
        slitLocationSlider.setEnabled( b );
        updateSeparationCheckbox();
    }

    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        updateUIToReflectSlitEnabled( slitPotential.isEnabled() );
        updateSeparationCheckbox();
    }

}
