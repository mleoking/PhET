/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 10:04:32 AM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class SlitControlPanel extends VerticalLayoutPanel {
    private IntensityModule intensityModule;

    public SlitControlPanel( final IntensityModule intensityModule ) {
        this.intensityModule = intensityModule;
        final JCheckBox leftSlit = new JCheckBox( "Detector on Left Slit" );
        leftSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityModule.setLeftDetectorEnabled( leftSlit.isSelected() );
            }
        } );
        add( leftSlit );

        final JCheckBox rightSlit = new JCheckBox( "Detector on right Slit" );
        rightSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityModule.setRightDetectorEnabled( rightSlit.isSelected() );
            }
        } );
        add( rightSlit );
    }

    public IntensityPanel getIntensityPanel() {
        return intensityModule.getIntensityPanel();
    }
}
