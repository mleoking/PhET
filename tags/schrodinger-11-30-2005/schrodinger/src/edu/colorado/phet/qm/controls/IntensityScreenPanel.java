/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.components.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.qm.view.piccolo.IntensityGraphic;

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

        final IntensityGraphic intensityGraphic = schrodingerControlPanel.getModule().getIntensityDisplay();
        JPanel inflationPanel = new HorizontalLayoutPanel();
        final JSpinner probabilityInflation = new JSpinner( new SpinnerNumberModel( intensityGraphic.getProbabilityScaleFudgeFactor(), 0.0, 1000, 0.1 ) );
//        probabilityInflation.setBorder( BorderFactory.createTitledBorder( "Probability Inflation" ) );
        probabilityInflation.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double val = ( (Number)probabilityInflation.getValue() ).doubleValue();
                intensityGraphic.setProbabilityScaleFudgeFactor( val );
            }
        } );
        inflationPanel.add( new JLabel( "Probability Inflation" ) );
        inflationPanel.add( probabilityInflation );
        super.addFullWidth( inflationPanel );

        JPanel pan = new HorizontalLayoutPanel();
        pan.add( new JLabel( "Waveform Decrement" ) );
        final JSpinner waveformDec = new JSpinner( new SpinnerNumberModel( intensityGraphic.getNormDecrement(), 0, 1.0, 0.1 ) );
//        waveformDec.setBorder( BorderFactory.createTitledBorder( "Waveform Decrement" ) );
        waveformDec.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double val = ( (Number)waveformDec.getValue() ).doubleValue();
                intensityGraphic.setNormDecrement( val );
            }
        } );
        pan.add( waveformDec );
        super.addFullWidth( pan );

        JPanel p3 = new HorizontalLayoutPanel();
        p3.add( new JLabel( "Multiplier" ) );
        final JSpinner mult = new JSpinner( new SpinnerNumberModel( intensityGraphic.getMultiplier(), 0, 1000, 5 ) );
        mult.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                intensityGraphic.setMultiplier( ( (Number)mult.getValue() ).intValue() );
            }
        } );
        p3.add( mult );
        super.addFullWidth( p3 );

        JPanel p4 = new HorizontalLayoutPanel();
        p4.add( new JLabel( "Opacity" ) );
        final JSpinner transparency = new JSpinner( new SpinnerNumberModel( intensityGraphic.getOpacity(), 0, 255, 1 ) );
        transparency.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int val = ( (Number)transparency.getValue() ).intValue();
                intensityGraphic.getDetectorSheet().setOpacity( val );
            }
        } );
        p4.add( transparency );
        addFullWidth( p4 );
    }
}
