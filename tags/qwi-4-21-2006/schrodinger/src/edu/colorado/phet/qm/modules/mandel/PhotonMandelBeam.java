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
    private MandelGunSet mandelGun;
    private DoublePhotonWave doublePhotonWave;

    public PhotonMandelBeam( MandelGunSet mandelGun, Photon photon ) {
        super( mandelGun, photon );
        this.mandelGun = mandelGun;
    }

    protected PhotonWave createCylinderWave() {
        if( doublePhotonWave == null ) {
            this.doublePhotonWave = new DoublePhotonWave( getGunGraphic().getSchrodingerModule(), getGunGraphic().getDiscreteModel(), this );
        }
        return doublePhotonWave;
    }

    public void setBeamParameters( MandelModule.BeamParam leftParam, MandelModule.BeamParam rightParam ) {
        doublePhotonWave.setBeamParameters( leftParam, rightParam );
    }

    public void setLeftMomentum( double momentum ) {
        doublePhotonWave.setLeftMomentum( momentum );
    }

    public void setRightMomentum( double momentum ) {
        doublePhotonWave.setRightMomentum( momentum );
    }
}
