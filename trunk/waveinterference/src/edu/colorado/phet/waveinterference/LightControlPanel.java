/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.view.*;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:15:03 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class LightControlPanel extends WaveInterferenceControlPanel {
    private LightModule lightModule;

    public LightControlPanel( LightModule lightModule ) {
        this.lightModule = lightModule;
        addControl( new MeasurementControlPanel( lightModule.getMeasurementToolSet() ) );
        addControl( new DetectorSetControlPanel( "E-Field", lightModule.getIntensityReaderSet(), lightModule.getLightSimulationPanel(), lightModule.getWaveModel(), lightModule.getLatticeScreenCoordinates(), lightModule.getClock() ) );
        addControl( new VerticalSeparator() );
        addControl( new ClearWaveControl( lightModule.getWaveModel() ) );
        addControlFullWidth( new VerticalSeparator() );

        addControl( new WaveRotateControl( lightModule.getRotationWaveGraphic() ) );
        addControlFullWidth( new VerticalSeparator() );
        addControl( new SlitControlPanel( lightModule.getSlitPotential() ) );
        addControlFullWidth( new VerticalSeparator() );
        addControl( new MultiOscillatorControlPanel( lightModule.getMultiOscillator(), "Light" ) );
//        addControl( new ScreenControlPanel( waterModule.getScreenNode() ) );
        addControl( new ReducedScreenControlPanel( lightModule.getScreenNode() ) );
    }
}
