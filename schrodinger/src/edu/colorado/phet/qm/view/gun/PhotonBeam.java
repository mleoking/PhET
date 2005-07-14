/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.CylinderWave;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:04:54 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class PhotonBeam extends HighIntensityBeam {
    private CylinderWave cylinderWave;
    private AbstractGun abstractGun;
    private Photon photon;

    public PhotonBeam( AbstractGun highIntensityGun, Photon photon ) {
        super( photon );
        this.photon = photon;
        this.abstractGun = highIntensityGun;
        cylinderWave = new CylinderWave( getGunGraphic().getSchrodingerModule(), getGunGraphic().getDiscreteModel() );
        super.setIntensityScale( 1.0 );
        cylinderWave.setIntensity( super.getTotalIntensity() );
        photon.addMomentumChangeListerner( new AbstractGun.MomentumChangeListener() {
            public void momentumChanged( double val ) {
                cylinderWave.setMomentum( val );
            }
        } );
    }

    private AbstractGun getGunGraphic() {
        return abstractGun;
    }

    public void setHighIntensityModeOn( boolean on ) {
        super.setHighIntensityModeOn( on );
        if( on ) {
            cylinderWave.setOn();
        }
        else {
            cylinderWave.setOff();
        }
    }

    public void setIntensity( double intensity ) {
        super.setIntensity( intensity );
        if( cylinderWave != null ) {
            cylinderWave.setIntensity( intensity );
        }
    }

    public void stepBeam() {
    }

    public Photon getPhoton() {
        return photon;
    }

}
