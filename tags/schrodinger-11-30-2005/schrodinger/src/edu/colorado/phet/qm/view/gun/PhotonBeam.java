/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.model.PhotonWave;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:04:54 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public class PhotonBeam extends HighIntensityBeam {
    private PhotonWave photonWave;
    private AbstractGun abstractGun;
    private Photon photon;
    private AbstractGun.MomentumChangeListener colorChangeHandler;

    public PhotonBeam( AbstractGun highIntensityGun, Photon photon ) {
        super( photon );
        this.photon = photon;
        this.abstractGun = highIntensityGun;
        photonWave = createCylinderWave();
        super.setIntensityScale( 1.0 );
        photonWave.setIntensity( super.getTotalIntensity() );
        photon.addMomentumChangeListerner( new AbstractGun.MomentumChangeListener() {
            public void momentumChanged( double val ) {
                photonWave.setMomentum( val );
            }
        } );

        colorChangeHandler = new AbstractGun.MomentumChangeListener() {
            public void momentumChanged( double val ) {
                handleColorChange();
            }
        };

        photon.addMomentumChangeListerner( colorChangeHandler );
        photonWave.setMomentum( photon.getStartPy() );
    }

    protected PhotonWave createCylinderWave() {
        return new PhotonWave( getGunGraphic().getSchrodingerModule(), getGunGraphic().getDiscreteModel() );
    }

    private void handleColorChange() {
        getGunGraphic().getSchrodingerModule().getDiscreteModel().clearWavefunction();
        getGunGraphic().getSchrodingerPanel().setDisplayPhotonColor( getPhoton() );
    }

    public AbstractGun.MomentumChangeListener getColorChangeHandler() {
        return colorChangeHandler;
    }

    protected AbstractGun getGunGraphic() {
        return abstractGun;
    }

    public void setHighIntensityModeOn( boolean on ) {
        super.setHighIntensityModeOn( on );
        if( on ) {
            photonWave.setOn();
            getGunGraphic().getSchrodingerPanel().setDisplayPhotonColor( getPhoton() );
        }
        else {
            photonWave.setOff();
        }
    }

    public void setIntensity( double intensity ) {
        super.setIntensity( intensity );
        if( photonWave != null ) {
            photonWave.setIntensity( intensity );
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
