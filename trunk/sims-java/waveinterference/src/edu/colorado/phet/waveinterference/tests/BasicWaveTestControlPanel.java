/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.ModelSlider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 22, 2006
 * Time: 7:11:02 PM
 * Copyright (c) Mar 22, 2006 by Sam Reid
 */
public class BasicWaveTestControlPanel extends ControlPanel {
    private BasicWaveTestModule testWaveModule;

    public BasicWaveTestControlPanel( final BasicWaveTestModule testWaveModule ) {
        this.testWaveModule = testWaveModule;
        final ModelSlider modelSlider = new ModelSlider( "Period", "sec", 0.01, 10, testWaveModule.getPeriod() );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                testWaveModule.setPeriod( modelSlider.getValue() );
            }
        } );
        addControl( modelSlider );

        final ModelSlider latticeWidth = new ModelSlider( "Lattice Width", "cells", 10, 300, testWaveModule.getLattice().getWidth() );
        latticeWidth.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int width = (int)latticeWidth.getValue();
                testWaveModule.setLatticeSize( width, testWaveModule.getLattice().getHeight() );
            }
        } );
        addControl( latticeWidth );

        final ModelSlider latticeHeight = new ModelSlider( "Lattice Height", "cells", 10, 300, testWaveModule.getLattice().getWidth() );
        latticeHeight.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int height = (int)latticeHeight.getValue();
                testWaveModule.setLatticeSize( testWaveModule.getLattice().getWidth(), height );
            }
        } );
        addControl( latticeHeight );
    }
}
