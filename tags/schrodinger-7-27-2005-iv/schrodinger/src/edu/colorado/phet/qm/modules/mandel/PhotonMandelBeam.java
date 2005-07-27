/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.model.PhotonWave;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.gun.PhotonBeam;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:46:23 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class PhotonMandelBeam extends PhotonBeam {
    private MandelGun mandelGun;

    public PhotonMandelBeam( MandelGun mandelGun, Photon photon ) {
        super( mandelGun, photon );
        this.mandelGun = mandelGun;
    }

    protected PhotonWave createCylinderWave() {
        return new DoublePhotonWave( getGunGraphic().getSchrodingerModule(), getGunGraphic().getDiscreteModel() );
    }

}
