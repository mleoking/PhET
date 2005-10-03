/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.view.piccolo.IntensityGraphic;

/**
 * User: Sam Reid
 * Date: Jul 18, 2005
 * Time: 8:16:11 AM
 * Copyright (c) Jul 18, 2005 by Sam Reid
 */

public class AutoFire implements IntensityGraphic.Listener {
    private SingleParticleGun gunGraphic;
    private IntensityGraphic intensityGraphic;
    private boolean autoFire = false;
    private ModelElement element;
    public static final double THRESHOLD = 0.015;
    private long lastFire = 0;

    public AutoFire( SingleParticleGun gunGraphic, IntensityGraphic intensityGraphic ) {
        this.gunGraphic = gunGraphic;
        this.intensityGraphic = intensityGraphic;
        intensityGraphic.addListener( this );
        element = new ModelElement() {
            public void stepInTime( double dt ) {
                checkDetection();
            }

        };
    }

    private void checkDetection() {
        System.out.println( "gunGraphic.getSchrodingerModule().getDiscreteModel().getWavefunction().getMagnitude() = " + gunGraphic.getSchrodingerModule().getDiscreteModel().getWavefunction().getMagnitude() );
        if( gunGraphic.getSchrodingerModule().getDiscreteModel().getWavefunction().getMagnitude() < THRESHOLD ) {
            if( System.currentTimeMillis() - lastFire > 500 ) {
                fire();
            }
        }
    }

    private void fire() {
        gunGraphic.clearAndFire();
        lastFire = System.currentTimeMillis();
    }

    public void detectionOccurred() {
        if( autoFire ) {
            fire();
        }
    }

    public boolean isAutoFire() {
        return autoFire;
    }

    public void setAutoFire( boolean autoFire ) {
        if( this.autoFire != autoFire ) {
            this.autoFire = autoFire;
            if( this.autoFire ) {
                if( intensityGraphic.getSchrodingerPanel().getDiscreteModel().getWavefunction().getMagnitude() == 0 ) {
                    fire();
                }
                gunGraphic.getSchrodingerModule().getModel().addModelElement( element );
            }
            else {
                gunGraphic.getSchrodingerModule().getModel().removeModelElement( element );
            }
        }

    }
}
