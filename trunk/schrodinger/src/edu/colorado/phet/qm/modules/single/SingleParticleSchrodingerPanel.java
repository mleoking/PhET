/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.view.gun.SingleParticleGunGraphic;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:20:13 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleSchrodingerPanel extends SchrodingerPanel {
    private SingleParticleGunGraphic abstractGun;

    public SingleParticleSchrodingerPanel( final SchrodingerModule module ) {
        super( module );
        abstractGun = new SingleParticleGunGraphic( this );
        setGunGraphic( abstractGun );
        getIntensityDisplay().setMultiplier( 1 );
        getIntensityDisplay().setProbabilityScaleFudgeFactor( 5 );
        getDetectorSheetPNode().setOpacity( 255 );
        getIntensityDisplay().setNormDecrement( 1.0 );

        getIntensityDisplay().addListener( new IntensityManager.Listener() {
            public void detectionOccurred() {
                module.getDiscreteModel().enableAllDetectors();
            }
        } );
    }

    public void reset() {
        super.reset();
        abstractGun.reset();
    }

    public void clearWavefunction() {
        super.clearWavefunction();
        abstractGun.reset();
    }

    public SingleParticleGunGraphic getAbstractGun() {
        return abstractGun;
    }
}
