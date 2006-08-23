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

public class LightModule extends WaveInterferenceModule {
    private WaveInterferenceModel waveInterferenceModel;
    private LightSimulationPanel lightSimulationPanel;
    private LightControlPanel lightControlPanel;
//    private PhetApplication application;

    public void setAsymmetricFeaturesEnabled( boolean b ) {
        lightControlPanel.setAsymmetricFeaturesEnabled( b );
    }

    public WaveInterferenceScreenUnits getScreenUnits() {
        return lightSimulationPanel.getScreenUnits();
    }

//    public void setPhetApplication( PhetApplication application ) {
//        this.application = application;
//    }

    public static class LightModel extends WaveInterferenceModel {
        public LightModel() {
            super.setDistanceUnits( "nm" );
            //todo: determined through experiment, depends on wavelength to frequency mapping in WavelengthControlPanel
            super.setPhysicalSize( 4200, 4200 );
            super.setTimeScale( 1 );
            super.setTimeUnits( "nanosec" );
        }
    }

    public LightModule() {
        super( WIStrings.getString( "light" ) );
        waveInterferenceModel = new LightModel();
        lightSimulationPanel = new LightSimulationPanel( this );
        lightControlPanel = new LightControlPanel( this );

        addModelElement( waveInterferenceModel );
        addModelElement( lightSimulationPanel );

        setSimulationPanel( lightSimulationPanel );
        setControlPanel( lightControlPanel );

        waveInterferenceModel.setInitialConditions();
    }

//    public void activate() {
//        super.activate();
//        updateMenus();
//    }
//
//    private void updateMenus() {
//        if( isActive() ) {
//            application.getPhetFrame().getm
//        }
//    }
//
//    public void deactivate() {
//        super.deactivate();
//        updateMenus();
//    }

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
        return lightSimulationPanel.getIntensityReaderSet();
    }

    public MeasurementToolSet getMeasurementToolSet() {
        return lightSimulationPanel.getMeasurementToolSet();
    }

    public LightSimulationPanel getLightSimulationPanel() {
        return lightSimulationPanel;
    }

    public RotationWaveGraphic getRotationWaveGraphic() {
        return lightSimulationPanel.getRotationWaveGraphic();
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return lightSimulationPanel.getLatticeScreenCoordinates();
    }

    public static void main( String[] args ) {
        new ModuleApplication().startApplication( args, new LightModule() );
    }

    public MultiOscillator getMultiOscillator() {
        return lightSimulationPanel.getMultiOscillator();
    }

    public ScreenNode getScreenNode() {
        return lightSimulationPanel.getScreenNode();
    }

    public WaveInterferenceModel getWaveInterferenceModel() {
        return waveInterferenceModel;
    }

    public void resetAll() {
        super.resetAll();
        getWaveInterferenceModel().reset();
        lightSimulationPanel.reset();
    }
}
