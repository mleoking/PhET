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
    private AbstractGun.MomentumChangeListener colorChangeHandler;

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

        colorChangeHandler = new AbstractGun.MomentumChangeListener() {
            public void momentumChanged( double val ) {
                getGunGraphic().getSchrodingerPanel().setDisplayPhotonColor( getPhoton() );
            }
        };
        photon.addMomentumChangeListerner( colorChangeHandler );
        cylinderWave.setMomentum( photon.getStartPy() );
    }


    public AbstractGun.MomentumChangeListener getColorChangeHandler() {
        return colorChangeHandler;
    }

    private AbstractGun getGunGraphic() {
        return abstractGun;
    }

    public void setHighIntensityModeOn( boolean on ) {
        super.setHighIntensityModeOn( on );
        if( on ) {
            cylinderWave.setOn();
            getGunGraphic().getSchrodingerPanel().setDisplayPhotonColor( getPhoton() );
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

    public void activate( HighIntensityGun gun ) {
        super.activate( gun );
        getGunGraphic().getSchrodingerPanel().setDisplayPhotonColor( getPhoton() );
    }

    public void deactivate( HighIntensityGun highIntensityGun ) {
        super.deactivate( highIntensityGun );
        getGunGraphic().getSchrodingerPanel().setDisplayPhotonColor( null );
    }

}
