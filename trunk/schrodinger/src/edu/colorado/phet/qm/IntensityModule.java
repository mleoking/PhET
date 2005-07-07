/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.clock.AbstractClock;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:06:16 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityModule extends SchrodingerModule {
    public IntensityModule( AbstractClock clock ) {
        super( "High Intensity", clock );
        setSchrodingerPanel( new IntensityPanel( this ) );
        setSchrodingerControlPanel( new IntensityControlPanel( this ) );
    }
}
