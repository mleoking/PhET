/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 30, 2005
 * Time: 9:17:04 AM
 * Copyright (c) Jun 30, 2005 by Sam Reid
 */

public class IntensityScreenPanel extends VerticalLayoutPanel {
    public IntensityScreenPanel( SchrodingerControlPanel schrodingerControlPanel ) {
        setBorder( BorderFactory.createTitledBorder( "Intensity Screen" ) );

        final IntensityDisplay intensityDisplay = schrodingerControlPanel.getModule().getIntensityDisplay();
        final JSpinner probabilityInflation = new JSpinner( new SpinnerNumberModel( 1.0, 0.1, 1000, 0.1 ) );
        probabilityInflation.setBorder( BorderFactory.createTitledBorder( "Probability Inflation" ) );
        probabilityInflation.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double val = ( (Number)probabilityInflation.getValue() ).doubleValue();
                intensityDisplay.setProbabilityScaleFudgeFactor( val );
            }
        } );
        super.addFullWidth( probabilityInflation );


        final JSpinner waveformDec = new JSpinner( new SpinnerNumberModel( 1.0, 0, 1.0, 0.1 ) );
        waveformDec.setBorder( BorderFactory.createTitledBorder( "Waveform Decrement" ) );
        waveformDec.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double val = ( (Number)waveformDec.getValue() ).doubleValue();
                intensityDisplay.setNormDecrement( val );
            }
        } );
        super.addFullWidth( waveformDec );
    }
}
