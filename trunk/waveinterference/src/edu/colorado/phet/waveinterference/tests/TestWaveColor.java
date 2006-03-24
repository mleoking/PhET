/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.SRRWavelengthSliderComponent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:17:23 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class TestWaveColor extends TestTopView {
    private SRRWavelengthSliderComponent slider;

    public TestWaveColor() {
        super( "Test Wave Color" );
        slider = new SRRWavelengthSliderComponent();
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateColor();
            }
        } );
        getControlPanel().addControl( slider );
        updateColor();
    }

    private void updateColor() {
        getSimpleLatticeGraphic().setColor( slider.getColor() );
    }

    public static void main( String[] args ) {
        ModuleApplication.startApplication( args, new TestWaveColor() );
    }
}
