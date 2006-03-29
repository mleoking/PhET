/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.tests.ModuleApplication;
import edu.colorado.phet.waveinterference.view.*;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:07:30 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class LightModule extends WaveInterferenceModule {
    private WaveInterferenceModel waveInterferenceModel;
    private LightSimulationPanel waterSimulationPanel;
    private LightControlPanel waterControlPanel;

    public LightModule() {
        super( "Light" );
        waveInterferenceModel = new WaveInterferenceModel();
        waterSimulationPanel = new LightSimulationPanel( this );
        waterControlPanel = new LightControlPanel( this );

        addModelElement( waveInterferenceModel );
        addModelElement( waterSimulationPanel );

        setSimulationPanel( waterSimulationPanel );
        setControlPanel( waterControlPanel );
    }

    public WaveModel getWaveModel() {
        return waveInterferenceModel.getWaveModel();
    }

    public SlitPotential getSlitPotential() {
        return waveInterferenceModel.getSlitPotential();
    }

    public Oscillator getSecondaryOscillator() {
        return waveInterferenceModel.getSecondaryOscillator();
    }

    public Oscillator getPrimaryOscillator() {
        return waveInterferenceModel.getPrimaryOscillator();
    }

    public IntensityReaderSet getIntensityReaderSet() {
        return waterSimulationPanel.getIntensityReaderSet();
    }

    public MeasurementToolSet getMeasurementToolSet() {
        return waterSimulationPanel.getMeasurementToolSet();
    }

//    public FaucetGraphic getPrimaryFaucetGraphic() {
//        return waterSimulationPanel.getPrimaryFaucetGraphic();
//    }

    public LightSimulationPanel getWaterSimulationPanel() {
        return waterSimulationPanel;
    }

    public RotationWaveGraphic getRotationWaveGraphic() {
        return waterSimulationPanel.getRotationWaveGraphic();
    }

//    public MultiFaucetDrip getMultiDrip() {
//        return waterSimulationPanel.getMultiDrip();
//    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return waterSimulationPanel.getLatticeScreenCoordinates();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new LightModule() );
    }

    public MultiOscillator getMultiOscillator() {
        return waterSimulationPanel.getMultiOscillator();
    }
}
