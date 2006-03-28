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
    private MultiDrip multiDrip;

    public MultiDripControlPanel( final MultiDrip multiDrip ) {
        this.multiDrip = multiDrip;
        oneDrip = new JRadioButton( "One Drip", multiDrip.isOneDrip() );
        oneDrip.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                multiDrip.setOneDrip();
                updateSpacingSlider();
            }
        } );
        twoDrips = new JRadioButton( "Two Drips", multiDrip.isTwoDrip() );
        twoDrips.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                multiDrip.setTwoDrips();
                updateSpacingSlider();
            }
        } );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( oneDrip );
        buttonGroup.add( twoDrips );
        add( oneDrip );
        add( twoDrips );
        spacingSlider = new ModelSlider( "Spacing", "m", 0, 50, multiDrip.getSpacing() );
        spacingSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                multiDrip.setSpacing( spacingSlider.getValue() );
            }
        } );
        add( spacingSlider );
        updateSpacingSlider();
    }

    private void updateSpacingSlider() {
        spacingSlider.setEnabled( multiDrip.isTwoDrip() );
    }
}
