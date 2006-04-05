/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.view.DetectorSetControlPanel;
import edu.colorado.phet.waveinterference.view.MeasurementControlPanel;
import edu.colorado.phet.waveinterference.view.MultiOscillatorControlPanel;
import edu.colorado.phet.waveinterference.view.SlitControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:15:03 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class SoundControlPanel extends WaveInterferenceControlPanel {
    private SoundModule waterModule;

    public SoundControlPanel( SoundModule soundModule ) {
        this.waterModule = soundModule;
        addControl( new ParticleSizeControl( soundModule.getSoundWaveGraphic() ) );
        addControl( new SlitControlPanel( soundModule.getSlitPotential() ) );
        addControl( new DetectorSetControlPanel( soundModule.getIntensityReaderSet(), soundModule.getSoundSimulationPanel(), soundModule.getWaveModel(), soundModule.getLatticeScreenCoordinates(), soundModule.getClock() ) );
        addControl( new MeasurementControlPanel( soundModule.getMeasurementToolSet() ) );
        addControl( new MultiOscillatorControlPanel( soundModule.getMultiOscillator() ) );
    }
}
