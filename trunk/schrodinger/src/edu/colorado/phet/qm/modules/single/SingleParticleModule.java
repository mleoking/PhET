/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:05:52 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleModule extends SchrodingerModule {
    public SingleParticleModule( SchrodingerApplication application, IClock clock ) {
        super( "Single Particles", application, clock );
        setDiscreteModel( new DiscreteModel() );
        final SingleParticlePanel schrodingerPanel = new SingleParticlePanel( this );
        setSchrodingerPanel( schrodingerPanel );
        setSchrodingerControlPanel( new SingleParticleControlPanel( this ) );
        getSchrodingerPanel().getIntensityDisplay().getDetectorSheet().getDetectorSheetControlPanel().setBrightnessSliderVisible( false );
        getSchrodingerPanel().getIntensityDisplay().getDetectorSheet().getDetectorSheetControlPanel().setFadeCheckBoxVisible( false );
        getSchrodingerPanel().getIntensityDisplay().getDetectorSheet().getDetectorSheetControlPanel().setTypeControlVisible( false );
        finishInit();
    }
}
