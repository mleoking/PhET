/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.tests.DetectorSetControlPanel;
import edu.colorado.phet.waveinterference.view.SlitControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:15:03 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class WaterControlPanel extends WaveInterferenceControlPanel {
    private WaterModule waterModule;

    public WaterControlPanel( WaterModule waterModule ) {
        this.waterModule = waterModule;
        addControl( new WaveRotateControl( waterModule.getRotationWaveGraphic() ) );
        addControl( new SlitControlPanel( waterModule.getSlitPotential() ) );
        addControl( new DetectorSetControlPanel( waterModule.getIntensityReaderSet(), waterModule.getWaterSimulationPanel(), waterModule.getWaveModel(), waterModule.getLatticeScreenCoordinates() ) );
//        addControl( new MeasurementControlPanel() );
//        addControl( new MultiDripControlPanel() );
    }
}
