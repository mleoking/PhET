/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.tests.ModuleApplication;
import edu.colorado.phet.waveinterference.tests.RotationWaveGraphic;
import edu.colorado.phet.waveinterference.view.FaucetGraphic;
import edu.colorado.phet.waveinterference.view.IntensityReaderSet;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.MeasurementToolSet;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:07:30 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class WaterModule extends WaveInterferenceModule {
    private WaterSimulationPanel waterSimulationPanel;
    private WaterModel waterModel;
    private WaterControlPanel waterControlPanel;

    public WaterModule() {
        super( "Water" );
        waterModel = new WaterModel();
        addModelElement( waterModel );
        waterSimulationPanel = new WaterSimulationPanel( this );
        addModelElement( waterSimulationPanel );
        waterControlPanel = new WaterControlPanel( this );

        setSimulationPanel( waterSimulationPanel );
        setControlPanel( waterControlPanel );
    }

    public WaterSimulationPanel getWaterSimulationPanel() {
        return waterSimulationPanel;
    }

    public static void main( String[] args ) {
        ModuleApplication.startApplication( args, new WaterModule() );
    }

    public RotationWaveGraphic getRotationWaveGraphic() {
        return waterSimulationPanel.getRotationWaveGraphic();
    }

    public WaveModel getWaveModel() {
        return waterModel.getWaveModel();
    }

    public SlitPotential getSlitPotential() {
        return waterModel.getSlitPotential();
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
        return waterModel.getPrimaryOscillator();
    }

    public FaucetGraphic getPrimaryFaucetGraphic() {
        return waterSimulationPanel.getPrimaryFaucetGraphic();
    }
}
