/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.SingleParticleGun;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:20:13 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticlePanel extends SchrodingerPanel {
//    private AutoFire autoFire;

    public SingleParticlePanel( SchrodingerModule module ) {
        super( module );
        setGunGraphic( new SingleParticleGun( this ) );
        getIntensityDisplay().setMultiplier( 1 );
        getIntensityDisplay().setProbabilityScaleFudgeFactor( 5 );
        getIntensityDisplay().setOpacity( 255 );
        getIntensityDisplay().setNormDecrement( 1.0 );

    }
}
