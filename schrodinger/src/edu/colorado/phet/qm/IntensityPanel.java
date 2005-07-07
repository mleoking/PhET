/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.view.RapidFireGun;
import edu.colorado.phet.qm.view.SchrodingerPanel;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:11 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityPanel extends SchrodingerPanel {
    public IntensityPanel( IntensityModule intensityModule ) {
        super( intensityModule );
        setGunGraphic( new RapidFireGun( this ) );
        getIntensityDisplay().setMultiplier( 100 );
        getIntensityDisplay().setProbabilityScaleFudgeFactor( 10 );
        getIntensityDisplay().setOpacity( 6 );
        getIntensityDisplay().setNormDecrement( 0.0 );
    }
}
