/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.QWIApplication;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.model.QWIModel;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:05:52 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleModule extends QWIModule {
    public SingleParticleModule( QWIApplication application, IClock clock ) {
        super( QWIStrings.getString( "single.particles" ), application, clock );
        setQWIModel( new QWIModel() );
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
