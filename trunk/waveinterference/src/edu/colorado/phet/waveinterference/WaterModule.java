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

public class WaterModule extends WaveInterferenceModule {
    private WaterSimulationPanel waterSimulationPanel;
    private WaveInterferenceModel waveInterferenceModel;
    private WaterControlPanel waterControlPanel;

    public WaterModule() {
        super( "Water" );
        waveInterferenceModel = new WaveInterferenceModel();
        addModelElement( waveInterferenceModel );
        waterSimulationPanel = new WaterSimulationPanel( this );
        addModelElement( waterSimulationPanel );
        waterControlPanel = new WaterControlPanel( this );

        setSimulationPanel( waterSimulationPanel );
        setControlPanel( waterControlPanel );
    }

    public WaterSimulationPanel getWaterSimulationPanel() {
        return waterSimulationPanel;
    }

    public RotationWaveGraphic getRotationWaveGraphic() {
        return waterSimulationPanel.getRotationWaveGraphic();
    }

    public WaveModel getWaveModel() {
        return waveInterferenceModel.getWaveModel();
    }

    public SlitPotential getSlitPotential() {
        return waveInterferenceModel.getSlitPotential();
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return waterSimulationPanel.getLatticeScreenCoordinates();
    }

    public IntensityReaderSet getIntensityReaderSet() {
        return waterSimulationPanel.getIntensityReaderSet();
    }

    public MeasurementToolSet getMeasurementToolSet() {
        return waterSimulationPanel.getMeasurementToolSet();
    }

    public Oscillator getPrimaryOscillator() {
        return waveInterferenceModel.getPrimaryOscillator();
    }

    public FaucetGraphic getPrimaryFaucetGraphic() {
        return waterSimulationPanel.getPrimaryFaucetGraphic();
    }

    public Oscillator getSecondaryOscillator() {
        return waveInterferenceModel.getSecondaryOscillator();
    }

    public MultiDrip getMultiDrip() {
        return waterSimulationPanel.getMultiDrip();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new WaterModule() );
    }
}
