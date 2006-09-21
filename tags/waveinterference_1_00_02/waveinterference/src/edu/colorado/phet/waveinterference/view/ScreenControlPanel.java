/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 25, 2006
 * Time: 12:30:28 AM
 * Copyright (c) Mar 25, 2006 by Sam Reid
 */

public class ScreenControlPanel extends VerticalLayoutPanel {
    private ScreenNode screenNode;

    public ScreenControlPanel( final ScreenNode screenNode ) {
        this.screenNode = screenNode;
        setBorder( BorderFactory.createTitledBorder( WIStrings.getString( "screen" ) ) );
        final JCheckBox enabled = new JCheckBox( WIStrings.getString( "show.screen" ), screenNode.isScreenEnabled() );
        enabled.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                screenNode.setScreenEnabled( enabled.isSelected() );
            }
        } );
        add( enabled );

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton curveMode = new JRadioButton( WIStrings.getString( "curve" ), screenNode.isCurveMode() );
        curveMode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                screenNode.setCurveMode();
            }
        } );
        JRadioButton intensityMode = new JRadioButton( WIStrings.getString( "intensity" ), screenNode.isIntensityMode() );
        intensityMode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                screenNode.setIntensityMode();
            }
        } );
        buttonGroup.add( curveMode );
        buttonGroup.add( intensityMode );
        add( intensityMode );
        add( curveMode );
    }
}
