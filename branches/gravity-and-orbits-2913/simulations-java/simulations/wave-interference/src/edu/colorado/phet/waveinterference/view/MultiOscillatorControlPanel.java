// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:38:54 PM
 */

public class MultiOscillatorControlPanel extends VerticalLayoutPanelWithDisable {
    private MultiOscillator multiOscillator;
    private WaveInterferenceScreenUnits units;
    private ModelSlider spacingSlider;
    private JRadioButton oneDrip;
    private JRadioButton twoDrips;

    public MultiOscillatorControlPanel( final MultiOscillator multiOscillator, String oneSourceName,String twoSourceName, WaveInterferenceScreenUnits units ) {
        this.multiOscillator = multiOscillator;
        this.units = units;
        setBorder( BorderFactory.createEtchedBorder() );
//        oneDrip = new JRadioButton( WIStrings.getString( "one.0" ) + name, multiOscillator.isOneSource() );
        oneDrip = new JRadioButton( oneSourceName, multiOscillator.isOneSource() );
        oneDrip.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                multiOscillator.setOneDrip();
                updateSpacingSlider();
            }
        } );
        twoDrips = new JRadioButton( twoSourceName, multiOscillator.isTwoSource() );
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
        spacingSlider = new ModelSlider( WIStrings.getString( "controls.spacing" ), "units.meters", 0, 30, multiOscillator.getSpacing() );
        spacingSlider.setModelLabels( units.toHashtable( new int[]{0, 15, 30}, 2 ) );
        spacingSlider.setTextFieldVisible( false );
        spacingSlider.setBorder( null );
        spacingSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                multiOscillator.setSpacing( spacingSlider.getValue() );
            }
        } );
        add( spacingSlider );

        multiOscillator.addListener( new MultiOscillator.Listener() {
            public void multiOscillatorChanged() {
                spacingSlider.setValue( multiOscillator.getSpacing() );
                oneDrip.setSelected( multiOscillator.isOneSource() );
                twoDrips.setSelected( multiOscillator.isTwoSource() );
                updateSpacingSlider();
            }
        } );

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
