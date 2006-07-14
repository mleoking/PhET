/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.modules.intensity.IntensityBeamPanel;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.view.gun.IntensityGunNode;
import edu.colorado.phet.qm.view.piccolo.QWIScreenNode;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:08:38 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGSchrodingerPanel extends IntensityBeamPanel {
    public DGSchrodingerPanel( IntensityModule intensityModule ) {
        super( intensityModule );
    }

    protected QWIScreenNode createSchrodingerScreenNode( QWIModule module ) {
        return new DGScreenNode( module, this );
    }

    protected IntensityGunNode createGun() {
        return new DGGun( this );
    }

    public DGGun getDGGunGraphic() {
        return (DGGun)super.getGunGraphic();
    }

    protected boolean useGunChooserGraphic() {
        return false;
    }
}
