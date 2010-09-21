package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.gun.GunControlPanel;

/**
 * User: Sam Reid
 * Date: Jul 14, 2006
 * Time: 11:24:47 AM
 */

public class DGGunControlPanel extends GunControlPanel {
    public DGGunControlPanel( QWIPanel schrodingerPanel ) {
        super( schrodingerPanel );
        getTitleLabel().setText( QWIResources.getString( "gun.title" ) );
    }

//    public double getVelocityRealUnits() {
//        return getGr;
//    }
}
