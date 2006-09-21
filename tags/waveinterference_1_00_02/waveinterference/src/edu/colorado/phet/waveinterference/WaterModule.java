/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.tests.ModuleApplication;
import edu.colorado.phet.waveinterference.util.WIStrings;
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

    public WaveInterferenceScreenUnits getScreenUnits() {
        return waterSimulationPanel.getScreenUnits();
    }

    static class WaterModel extends WaveInterferenceModel {
        public WaterModel() {
            setDistanceUnits( "cm" );
            setPhysicalSize( 10, 10 );
        }

    }

    public WaterModule() {
        super( WIStrings.getString( "water" ) );
        waveInterferenceModel = new WaterModel();
        waterSimulationPanel = new WaterSimulationPanel( this );
        waterControlPanel = new WaterControlPanel( this );

        addModelElement( waveInterferenceModel );
        addModelElement( waterSimulationPanel );

        setSimulationPanel( waterSimulationPanel );
        setControlPanel( waterControlPanel );

        waveInterferenceModel.setInitialConditions();//because some are set from controls in setSimulationPanel
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

    public FaucetGraphic getPrimaryFaucetGraphic() {
        return waterSimulationPanel.getPrimaryFaucetGraphic();
    }

    public WaterSimulationPanel getWaterSimulationPanel() {
        return waterSimulationPanel;
    }

    public RotationWaveGraphic getRotationWaveGraphic() {
        return waterSimulationPanel.getRotationWaveGraphic();
    }

    public MultiFaucetDrip getMultiDrip() {
        return waterSimulationPanel.getMultiDrip();
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return waterSimulationPanel.getLatticeScreenCoordinates();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new WaterModule() );
    }

    public WaveInterferenceModel getWaveInterferenceModel() {
        return waveInterferenceModel;
    }

    public void resetAll() {
        super.resetAll();
        getWaveInterferenceModel().reset();
        waterSimulationPanel.reset();
    }
}
