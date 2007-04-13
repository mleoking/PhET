/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.phetcommon.ImageComboBox;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 9:04:25 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */
public abstract class IntensityBeam extends ImageComboBox.Item {
    private boolean highIntensityModeOn;
    private double intensity = 0.0;
    private double intensityScale = 0.35;
    private GunParticle gunParticle;

    public IntensityBeam( GunParticle gunParticle ) {
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

    public double getIntensity() {
        return intensity;
    }

    public void deactivate( IntensityGunNode intensityGun ) {
        setHighIntensityModeOn( false );
        gunParticle.deactivate( intensityGun );
    }

    public void activate( IntensityGunNode gun ) {
        gunParticle.activate( gun );
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

    public Point getGunLocation() {
        return new Point( -10, 35 );
    }

    public void reset() {
        setIntensity( 0.0 );
//        gunParticle.reset();
    }
}
