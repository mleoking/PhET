// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.modules.intensity.IntensityBeamPanel;
import edu.colorado.phet.quantumwaveinterference.modules.intensity.IntensityModule;
import edu.colorado.phet.quantumwaveinterference.view.gun.IntensityGunNode;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.QWIScreenNode;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:08:38 PM
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
