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

public class WaterControlPanel extends WaveInterferenceControlPanel {
    private WaterModule waterModule;

    public WaterControlPanel( WaterModule waterModule ) {
        this.waterModule = waterModule;
        addControl( new MeasurementControlPanel( waterModule.getMeasurementToolSet() ) );

        addControl( new DetectorSetControlPanel( WIStrings.getString( "water.level" ), waterModule.getIntensityReaderSet(), waterModule.getWaterSimulationPanel(), waterModule.getWaveModel(), waterModule.getLatticeScreenCoordinates(), waterModule.getClock() ) );

        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );
        addControl( new ResetModuleControl( waterModule ) );
        addControlFullWidth( new VerticalSeparator( PAD_FOR_RESET_BUTTON ) );

        addControl( new WaveRotateControl( waterModule.getRotationWaveGraphic() ) );
        addVerticalSpace();

        addControl( new MultiDripControlPanel( waterModule.getMultiDrip(), waterModule.getScreenUnits() ) );
        addControlFullWidth( new VerticalSeparator() );

        addControl( new SlitControlPanel( waterModule.getSlitPotential(), waterModule.getScreenUnits() ) );
    }

}
