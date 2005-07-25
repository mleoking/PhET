/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.view.IntensityDisplay;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 8:16:11 AM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class AutoFire implements IntensityDisplay.Listener {
    private SingleParticleGun gunGraphic;
    private IntensityDisplay intensityDisplay;
    private boolean autoFire = false;

    public AutoFire( SingleParticleGun gunGraphic, IntensityDisplay intensityDisplay ) {
        this.gunGraphic = gunGraphic;
        this.intensityDisplay = intensityDisplay;
        intensityDisplay.addListener( this );
    }

    public void detectionOccurred() {
        if( autoFire ) {
            gunGraphic.clearAndFire();
        }
    }

    public boolean isAutoFire() {
        return autoFire;
    }

    public void setAutoFire( boolean autoFire ) {
        if( this.autoFire != autoFire ) {
            this.autoFire = autoFire;
            if( this.autoFire ) {
                if( intensityDisplay.getSchrodingerPanel().getDiscreteModel().getWavefunction().getMagnitude() == 0 ) {
                    gunGraphic.clearAndFire();
                }
            }
        }
    }
}
