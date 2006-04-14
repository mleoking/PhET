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
    private LightModule waterModule;

    public LightControlPanel( LightModule waterModule ) {
        this.waterModule = waterModule;
        addControl( new MeasurementControlPanel( waterModule.getMeasurementToolSet() ) );
        addControl( new DetectorSetControlPanel( waterModule.getIntensityReaderSet(), waterModule.getWaterSimulationPanel(), waterModule.getWaveModel(), waterModule.getLatticeScreenCoordinates(), waterModule.getClock() ) );
        addControlFullWidth( new VerticalSeparator() );
        addControl( new WaveRotateControl( waterModule.getRotationWaveGraphic() ) );
        addControl( new SlitControlPanel( waterModule.getSlitPotential() ) );


        addControl( new MultiOscillatorControlPanel( waterModule.getMultiOscillator() ) );
//        addControl( new ScreenControlPanel( waterModule.getScreenNode() ) );
        addControl( new ReducedScreenControlPanel( waterModule.getScreenNode() ) );
    }
}
