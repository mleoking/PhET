/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:08:38 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGSchrodingerPanel extends HighIntensitySchrodingerPanel {
    public DGSchrodingerPanel( IntensityModule intensityModule ) {
        super( intensityModule );
    }

    protected HighIntensityGunGraphic createGun() {
        return new DGGun( this );
    }

    public DGGun getDGGunGraphic() {
        return (DGGun)super.getGunGraphic();
    }
}
