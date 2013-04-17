// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.modules.mandel;

import edu.colorado.phet.quantumwaveinterference.model.PhotonWave;
import edu.colorado.phet.quantumwaveinterference.view.gun.Photon;
import edu.colorado.phet.quantumwaveinterference.view.gun.PhotonBeam;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:46:23 AM
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
