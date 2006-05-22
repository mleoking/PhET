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
    private MultiOscillatorControlPanel multiOscillatorControlPanel;
    private SlitControlPanel slitControlPanel;
    private ReducedScreenControlPanel screenControlPanel;

    public LightControlPanel( LightModule lightModule ) {
        this.lightModule = lightModule;
        addControl( new MeasurementControlPanel( lightModule.getMeasurementToolSet() ) );
        addControl( new DetectorSetControlPanel( "E-Field", lightModule.getIntensityReaderSet(), lightModule.getLightSimulationPanel(), lightModule.getWaveModel(), lightModule.getLatticeScreenCoordinates(), lightModule.getClock() ) );
        addControl( new VerticalSeparator() );
        addControl( new ClearWaveControl( lightModule.getWaveModel() ) );
        addControlFullWidth( new VerticalSeparator() );

        addControl( new WaveRotateControl3D( lightModule.getWaveInterferenceModel(), lightModule.getRotationWaveGraphic() ) );
        addControlFullWidth( new VerticalSeparator() );
        slitControlPanel = new SlitControlPanel( lightModule.getSlitPotential() );
        addControl( slitControlPanel );
        addControlFullWidth( new VerticalSeparator() );
        multiOscillatorControlPanel = new MultiOscillatorControlPanel( lightModule.getMultiOscillator(), "Light" );
        addControl( multiOscillatorControlPanel );
//        addControl( new ScreenControlPanel( waterModule.getScreenNode() ) );

        //enable these lines to add a "Show Screen" button to the control panel.
//        screenControlPanel = new ReducedScreenControlPanel( lightModule.getScreenNode() );
//        addControl( screenControlPanel );
    }

    public void setAsymmetricFeaturesEnabled( boolean b ) {
        multiOscillatorControlPanel.setEnabled( b );
        slitControlPanel.setEnabled( b );
        screenControlPanel.setEnabled( b );
    }
}
