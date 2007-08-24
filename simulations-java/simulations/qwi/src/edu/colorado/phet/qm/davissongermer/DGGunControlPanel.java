package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.gun.GunControlPanel;

/**
 * User: Sam Reid
 * Date: Jul 14, 2006
 * Time: 11:24:47 AM
 */

public class DGGunControlPanel extends GunControlPanel {
    public DGGunControlPanel( QWIPanel schrodingerPanel ) {
        super( schrodingerPanel );
        getTitleLabel().setText( QWIStrings.getString( "gun.title" ) );
    }

//    public double getVelocityRealUnits() {
//        return getGr;
//    }
}
