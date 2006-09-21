/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.waveinterference.view.OscillatorControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:42:07 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class TestFrequencyAmplitudeControlPanel extends TestTopView {
    public TestFrequencyAmplitudeControlPanel() {
        super( "Freq & Amp" );
        OscillatorControlPanel oscillatorControlPanel = new OscillatorControlPanel( getOscillator() );
        getControlPanel().addControl( oscillatorControlPanel );
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new TestFrequencyAmplitudeControlPanel() );
    }
}
