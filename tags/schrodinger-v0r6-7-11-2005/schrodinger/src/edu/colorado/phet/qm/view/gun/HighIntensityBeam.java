/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.phetcommon.ImageComboBox;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:04:25 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public abstract class HighIntensityBeam extends ImageComboBox.Item {
    private boolean highIntensityModeOn;
    private double intensity = 0.0;
    private double intensityScale = 0.35;
    private GunParticle gunParticle;

    public HighIntensityBeam( GunParticle gunParticle ) {
        super( gunParticle.getLabel(), gunParticle.getImageLocation() );
        this.gunParticle = gunParticle;
        setIntensity( 0.0 );
    }

    public void setHighIntensityModeOn( boolean highIntensityModeOn ) {
        this.highIntensityModeOn = highIntensityModeOn;
    }

    public void setIntensity( double intensity ) {
        this.intensity = intensity;
        gunParticle.setIntensityScale( this.intensity * intensityScale );
    }

    public void deactivate( HighIntensityGun highIntensityGun ) {
        setHighIntensityModeOn( false );
        gunParticle.deactivate( highIntensityGun );
    }

    public void activate( HighIntensityGun gun ) {
        gunParticle.setup( gun );
    }

    public GunParticle getGunParticle() {
        return gunParticle;
    }

    public abstract void stepBeam();

    public boolean isHighIntensityModeOn() {
        return highIntensityModeOn;
    }

    public double getTotalIntensity() {
        return intensity * intensityScale;
    }

    public void setIntensityScale( double intensityScale ) {
        this.intensityScale = intensityScale;
    }
}
