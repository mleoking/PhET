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
    private AbstractGunGraphic abstractGunGraphic;
    private Photon photon;
    private AbstractGunGraphic.MomentumChangeListener colorChangeHandler;

    public PhotonBeam( AbstractGunGraphic highIntensityGunGraphic, Photon photon ) {
        super( photon );
        this.photon = photon;
        this.abstractGunGraphic = highIntensityGunGraphic;
        photonWave = createCylinderWave();
        super.setIntensityScale( 1.0 );
        photonWave.setIntensity( super.getTotalIntensity() );
        photon.addMomentumChangeListerner( new AbstractGunGraphic.MomentumChangeListener() {
            public void momentumChanged( double val ) {
                photonWave.setMomentum( val );
//                System.out.println( "Momentum: val = " + val );
            }
        } );

        colorChangeHandler = new AbstractGunGraphic.MomentumChangeListener() {
            public void momentumChanged( double val ) {
                if( getGunParticle().isActive() ) {
                    handleColorChange();
                }
            }
        };

        photon.addMomentumChangeListerner( colorChangeHandler );
        photonWave.setMomentum( photon.getStartPy() );
    }

    protected PhotonWave createCylinderWave() {
        return new PhotonWave( getGunGraphic().getSchrodingerModule(), getGunGraphic().getDiscreteModel() );
    }

    private void handleColorChange() {
        getGunGraphic().getSchrodingerModule().getQWIModel().clearWavefunction();
        getGunGraphic().getSchrodingerPanel().setPhoton( getPhoton() );
    }

    public AbstractGunGraphic.MomentumChangeListener getColorChangeHandler() {
        return colorChangeHandler;
    }

    protected AbstractGunGraphic getGunGraphic() {
        return abstractGunGraphic;
    }

    public void setHighIntensityModeOn( boolean on ) {
        super.setHighIntensityModeOn( on );
        if( on ) {
            photonWave.setOn();
            getGunGraphic().getSchrodingerPanel().setPhoton( getPhoton() );
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

    public void activate( HighIntensityGunGraphic gun ) {
        super.activate( gun );
        getGunGraphic().getSchrodingerPanel().setPhoton( getPhoton() );
    }

    public void deactivate( HighIntensityGunGraphic highIntensityGun ) {
        super.deactivate( highIntensityGun );
        getGunGraphic().getSchrodingerPanel().setPhoton( null );
    }

}
