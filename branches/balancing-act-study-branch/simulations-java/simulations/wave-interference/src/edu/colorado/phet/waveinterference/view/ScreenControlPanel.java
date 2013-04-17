// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 25, 2006
 * Time: 12:30:28 AM
 */

public class ScreenControlPanel extends VerticalLayoutPanel {
    private ScreenNode screenNode;

    public ScreenControlPanel( final ScreenNode screenNode ) {
        this.screenNode = screenNode;
        setBorder( BorderFactory.createTitledBorder( WIStrings.getString( "light.screen" ) ) );
        final JCheckBox enabled = new JCheckBox( WIStrings.getString( "light.show-screen" ), screenNode.isScreenEnabled() );
        enabled.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                screenNode.setScreenEnabled( enabled.isSelected() );
            }
        } );
        add( enabled );

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton curveMode = new JRadioButton( WIStrings.getString( "light.curve" ), screenNode.isCurveMode() );
        curveMode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                screenNode.setCurveMode();
            }
        } );
        JRadioButton intensityMode = new JRadioButton( WIStrings.getString( "readout.intensity" ), screenNode.isIntensityMode() );
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
