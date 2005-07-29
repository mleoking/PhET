/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.view.WavefunctionGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Jul 29, 2005
 * Time: 9:34:53 AM
 * Copyright (c) Jul 29, 2005 by Sam Reid
 */

public class ResolutionControl extends AdvancedPanel {
    private SchrodingerControlPanel schrodingerControlPanel;

    public ResolutionControl( SchrodingerControlPanel schrodingerControlPanel ) {
        super( "Resolution>>", "Resolution<<" );
        this.schrodingerControlPanel = schrodingerControlPanel;

        JLabel screenSizeLabel = new JLabel( "Grid Resolution" );
        addControl( screenSizeLabel );
        int defaultWidth = 60;
        final JSpinner screenSize = new JSpinner( new SpinnerNumberModel( defaultWidth, 5, 200, 5 ) );
        getSchrodingerModule().setWaveSize( defaultWidth );

        screenSize.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer value = (Integer)screenSize.getValue();
                getSchrodingerModule().setWaveSize( value.intValue() );
            }
        } );
        addControl( screenSize );

        JLabel numSkip = new JLabel( "Time Step" );
        addControl( numSkip );
        final JSpinner frameSkip = new JSpinner( new SpinnerNumberModel( WavefunctionGraphic.numIterationsBetwenScreenUpdate, 1, 20, 1 ) );
        frameSkip.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer val = (Integer)frameSkip.getValue();
                WavefunctionGraphic.numIterationsBetwenScreenUpdate = val.intValue();
            }
        } );
        addControl( frameSkip );
    }

    private int getWaveWidth() {
        return getSchrodingerModule().getDiscreteModel().getGridWidth();
    }

    private SchrodingerModule getSchrodingerModule() {
        return schrodingerControlPanel.getModule();
    }
}
