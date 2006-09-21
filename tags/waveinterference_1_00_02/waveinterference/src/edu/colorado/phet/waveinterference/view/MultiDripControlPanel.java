/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

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

        oneDrip = new JRadioButton( WIStrings.getString( "one.drip" ), multiFaucetDrip.isOneDrip() );
        oneDrip.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                multiFaucetDrip.setOneDrip();
                updateSpacingSliderEnable();
            }
        } );
        twoDrips = new JRadioButton( WIStrings.getString( "two.drips" ), multiFaucetDrip.isTwoDrip() );
        twoDrips.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                multiFaucetDrip.setTwoDrips();
                updateSpacingSliderEnable();
            }
        } );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( oneDrip );
        buttonGroup.add( twoDrips );
        add( oneDrip );
        add( twoDrips );
        spacingSlider = new ModelSlider( WIStrings.getString( "spacing" ), WIStrings.getString( "m" ), 0, 30, multiFaucetDrip.getSpacing() );
        spacingSlider.setModelLabels( screenUnits.toHashtable( new int[]{0, 15, 30}, 2 ) );
        spacingSlider.setTextFieldVisible( false );
        spacingSlider.setBorder( null );
        spacingSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                multiFaucetDrip.setSpacing( spacingSlider.getValue() );
            }
        } );
        add( spacingSlider );
        updateSpacingSliderEnable();
        Oscillator.Adapter listener = new Oscillator.Adapter() {
            public void locationChanged() {
                updateSpacingReadout();
            }
        };
        multiFaucetDrip.getPrimary().getOscillator().addListener( listener );
        multiFaucetDrip.getSecondary().getOscillator().addListener( listener );

        multiFaucetDrip.addListener( new MultiFaucetDrip.Listener() {
            public void spacingChanged() {
            }

            public void dropCountChanged() {
                oneDrip.setSelected( multiFaucetDrip.isOneDrip() );
                twoDrips.setSelected( multiFaucetDrip.isTwoDrip() );
                updateSpacingReadout();
                updateSpacingSliderEnable();
            }
        } );
    }

    private void updateSpacingReadout() {//todo this model is a bit ugly, but works correctly.
        ArrayList sav = new ArrayList();
        while( spacingSlider.numChangeListeners() > 0 ) {
            ChangeListener c = spacingSlider.getChangeListener( 0 );
            spacingSlider.removeChangeListener( c );
            sav.add( c );
        }
        spacingSlider.setValue( getDistanceBetweenOscillators() / 2 );
        for( int i = 0; i < sav.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)sav.get( i );
            spacingSlider.addChangeListener( changeListener );
        }
    }

    private double getDistanceBetweenOscillators() {
        Point2D a = multiFaucetDrip.getPrimary().getOscillator().getCenter();
        Point2D b = multiFaucetDrip.getSecondary().getOscillator().getCenter();
        return a.distance( b );
    }

    private void updateSpacingSliderEnable() {
        spacingSlider.setEnabled( multiFaucetDrip.isTwoDrip() );
    }
}
