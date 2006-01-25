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
        final SingleParticleSchrodingerPanel schrodingerSchrodingerPanel = new SingleParticleSchrodingerPanel( this );
        setSchrodingerPanel( schrodingerSchrodingerPanel );
        setSchrodingerControlPanel( new SingleParticleControlPanel( this ) );
        getSchrodingerPanel().getDetectorSheetPNode().getDetectorSheetControlPanel().setBrightnessSliderVisible( false );
        getSchrodingerPanel().getDetectorSheetPNode().getDetectorSheetControlPanel().setFadeCheckBoxVisible( false );
        getSchrodingerPanel().getDetectorSheetPNode().getDetectorSheetControlPanel().setTypeControlVisible( false );
        getSchrodingerPanel().getDetectorSheetPNode().updatePSwing();
        finishInit();
    }
}
