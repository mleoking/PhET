/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.util.WIStrings;
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
        addControl( new DetectorSetControlPanel( WIStrings.getString( "e.field" ), lightModule.getIntensityReaderSet(), lightModule.getLightSimulationPanel(), lightModule.getWaveModel(), lightModule.getLatticeScreenCoordinates(), lightModule.getClock() ) );

        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );
        addControl( new ResetModuleControl( lightModule ) );
        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

        addControl( new WaveRotateControl3D( lightModule.getWaveInterferenceModel(), lightModule.getRotationWaveGraphic() ) );
        addVerticalSpace();

        multiOscillatorControlPanel = new MultiOscillatorControlPanel( lightModule.getMultiOscillator(), WIStrings.getString( "light" ), lightModule.getScreenUnits() );
        addControl( multiOscillatorControlPanel );
        addVerticalSpace();

        slitControlPanel = new SlitControlPanel( lightModule.getSlitPotential(), lightModule.getScreenUnits() );
        addControl( slitControlPanel );

        //enable these lines to add a "Show Screen" button to the control panel.
//        screenControlPanel = new ReducedScreenControlPanel( lightModule.getScreenNode() );
//        addControl( screenControlPanel );
    }

    public void setAsymmetricFeaturesEnabled( boolean b ) {
        multiOscillatorControlPanel.setEnabled( b );
        slitControlPanel.setEnabled( b );
//        screenControlPanel.setEnabled( b );
    }
}
