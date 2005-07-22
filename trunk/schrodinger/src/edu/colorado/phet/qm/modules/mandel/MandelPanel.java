/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.modules.intensity.IntensityPanel;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:03:32 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelPanel extends IntensityPanel {
    private MandelModule mandelModule;

    public MandelPanel( MandelModule mandelModule ) {
        super( mandelModule );
        this.mandelModule = mandelModule;

        setGunGraphic( new MandelGun( this ) );
    }
}
